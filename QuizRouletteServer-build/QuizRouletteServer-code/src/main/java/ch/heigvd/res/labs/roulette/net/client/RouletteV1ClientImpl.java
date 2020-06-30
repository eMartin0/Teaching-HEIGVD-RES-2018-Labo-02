package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;


/**
 * This class implements the client side of the protocol specification (version 1).
 * 
 * @author Olivier Liechti, Eloïse Martin
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

  private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
  /** Connection to the server **/
  protected Socket mySocket = null;
  /** Buffered printer to send data to the server **/
  protected PrintWriter myPWriter = null;
  /** Buffer reader to receive data from the server **/
  protected BufferedReader myBReader = null;

  @Override
  public void connect(String server, int port) throws IOException {
    mySocket = new Socket(server, port);
    myPWriter = new PrintWriter(new OutputStreamWriter(mySocket.getOutputStream()));
    myBReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
    myBReader.readLine();
  }

  @Override
  public void disconnect() throws IOException {
    if (mySocket == null) {
      return;
    }
    myPWriter.println(RouletteV1Protocol.CMD_BYE);
    myPWriter.flush();
    mySocket.close();
    mySocket = null;
    myBReader.close();
    myPWriter.close();
  }

  @Override
  public boolean isConnected() {
    return mySocket != null;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    myPWriter.println(RouletteV1Protocol.CMD_LOAD);
    myPWriter.flush();
    myBReader.readLine();
    myPWriter.println(fullname);
    myPWriter.flush();
    myPWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    myPWriter.flush();
    myBReader.readLine();
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    myPWriter.println(RouletteV1Protocol.CMD_LOAD);
    myPWriter.flush();
    myBReader.readLine();
    for (int i = 0; i < students.size(); i++) {
      myPWriter.println(students.get(i).getFullname());
    }
    myPWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    myPWriter.flush();
    myBReader.readLine();
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    myPWriter.println(RouletteV1Protocol.CMD_RANDOM);
    myPWriter.flush();
    RandomCommandResponse randomResponse = JsonObjectMapper.parseJson(myBReader.readLine(), RandomCommandResponse.class);
    if (randomResponse.getError() != null) {
      throw new EmptyStoreException();
    }
    return new Student(randomResponse.getFullname());
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    myPWriter.println(RouletteV1Protocol.CMD_INFO);
    myPWriter.flush();
    InfoCommandResponse myInfo = JsonObjectMapper.parseJson(myBReader.readLine(), InfoCommandResponse.class);
    return myInfo.getNumberOfStudents();
  }

  @Override
  public String getProtocolVersion() throws IOException {
    myPWriter.println(RouletteV1Protocol.CMD_INFO);
    myPWriter.flush();
    InfoCommandResponse myInfo = JsonObjectMapper.parseJson(myBReader.readLine(), InfoCommandResponse.class);
    return myInfo.getProtocolVersion();
  }

}
