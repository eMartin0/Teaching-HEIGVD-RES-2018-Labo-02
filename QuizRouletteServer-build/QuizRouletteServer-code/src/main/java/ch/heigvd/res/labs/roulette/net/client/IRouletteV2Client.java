package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Olivier Liechti, Eloïse Martin
 */
public interface IRouletteV2Client extends IRouletteV1Client {

  /**
   * Clears the students data store, by invoking the CLEAR command defined in
   * the protocol (version 2).
   * 
   * @throws IOException 
   */
  public void clearDataStore() throws IOException;

  /**
   * Invokes the LIST command defined in the protocol (version 2), parses the
   * response and converts it into a list of Student objects (using the JsonObjectMapper
   * class and the StudentsList class).
   * 
   * @return the list of students currently in the store
   * @throws IOException 
   */
  public List<Student> listStudents() throws IOException;

  /**
   * Gets the last number of student successfully loaded to the server
   * @return the number of student successfully loaded to the server
   * @throws IOException
   */
  public int getNumberOfStudentAdded() throws IOException;

  /**
   * Gets the number of commands sent to the server if not done, it's the internal value
   * if the connection has been ended, it's the server value (same number)
   * @return 
   * @throws IOException
   */
  public int getNumberOfCommands() throws  IOException;

  /**
   *
   * @return
   * @throws IOException
   */
  public boolean checkSuccessOfCommand() throws  IOException;

}
