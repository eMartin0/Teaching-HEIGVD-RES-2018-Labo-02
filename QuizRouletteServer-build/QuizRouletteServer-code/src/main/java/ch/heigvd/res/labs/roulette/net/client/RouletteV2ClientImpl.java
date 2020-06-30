package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti, Eloïse Martin
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

  private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

  /** Keeps the last cmd status **/
  private boolean lastCommandStatus;
  /** Keeps the last number of student succesfully added **/
  protected int lastNewStudents;
  /** Keeps count of the command sent to the server **/
  protected int nbCommandsSent;

  @Override
  public void connect(String server, int port) throws IOException {
    super.connect(server, port);
    lastCommandStatus = true;
    lastNewStudents = 0;
    nbCommandsSent = 0;
  }

  @Override
  public void disconnect() throws IOException {
    if (mySocket == null) {
      return;
    }

    myPWriter.println(RouletteV2Protocol.CMD_BYE);
    myPWriter.flush();

    ByeCommandResponse byeCmdResponse = JsonObjectMapper.parseJson(myBReader.readLine(), ByeCommandResponse.class);
    lastCommandStatus = byeCmdResponse.getStatus().equals("success");
    nbCommandsSent = byeCmdResponse.getNbCommands();

    mySocket.close();
    mySocket = null;
    myBReader.close();
    myPWriter.close();

    nbCommandsSent++;
  }

  @Override
  public void loadStudent(String fullname) throws IOException {
    myPWriter.println(RouletteV2Protocol.CMD_LOAD);
    myPWriter.flush();
    myBReader.readLine();
    myPWriter.println(fullname);
    myPWriter.flush();
    myPWriter.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    myPWriter.flush();
    LoadCommandResponse loadCommandResponse = JsonObjectMapper.parseJson(myBReader.readLine(), LoadCommandResponse.class);
    lastCommandStatus = loadCommandResponse.getStatus().equals("success");
    lastNewStudents = loadCommandResponse.getNumberOfNewStudents();
    nbCommandsSent++;
  }

  @Override
  public void loadStudents(List<Student> students) throws IOException {
    myPWriter.println(RouletteV2Protocol.CMD_LOAD);
    myPWriter.flush();
    myBReader.readLine();
    for (int i = 0; i < students.size(); i++) {
      myPWriter.println(students.get(i).getFullname());
    }
    myPWriter.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
    myPWriter.flush();
    LoadCommandResponse loadCommandResponse = JsonObjectMapper.parseJson(myBReader.readLine(), LoadCommandResponse.class);
    lastCommandStatus = loadCommandResponse.getStatus().equals("success");
    lastNewStudents = loadCommandResponse.getNumberOfNewStudents();
    nbCommandsSent++;
  }

  @Override
  public Student pickRandomStudent() throws EmptyStoreException, IOException {
    Student student = super.pickRandomStudent();
    nbCommandsSent++;
    return student;
  }

  @Override
  public int getNumberOfStudents() throws IOException {
    int retVal = super.getNumberOfStudents();
    nbCommandsSent++;
    return retVal;
  }

  @Override
  public String getProtocolVersion() throws IOException {
    String retVal = super.getProtocolVersion();
    nbCommandsSent++;
    return retVal;
  }

  @Override
  public void clearDataStore() throws IOException {
    myPWriter.println(RouletteV2Protocol.CMD_CLEAR);
    myPWriter.flush();
    myBReader.readLine();
    nbCommandsSent++;
  }

  @Override
  public List<Student> listStudents() throws IOException {
    myPWriter.println(RouletteV2Protocol.CMD_LIST);
    myPWriter.flush();
    StudentsList studentsList = JsonObjectMapper.parseJson(myBReader.readLine(), StudentsList.class);
    nbCommandsSent++;
    return studentsList.getStudents();
  }

  @Override
  public int getNumberOfStudentAdded() throws IOException {
    return lastNewStudents;
  }

  @Override
  public int getNumberOfCommands() throws  IOException {
    return nbCommandsSent;
  }

  @Override
  public boolean checkSuccessOfCommand() throws  IOException {
    return lastCommandStatus;
  }

}
