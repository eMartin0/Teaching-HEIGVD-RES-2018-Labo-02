package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import java.util.ArrayList;
import java.util.List;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.io.IOException;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Olivier Liechti, Eloïse Martin
 */
public class RouletteV2eMartin0Test {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = "eMartin0")
  public void theTestRouletteServerShouldRunDuringTests() throws IOException {
    assertTrue(roulettePair.getServer().isRunning());
  }

  @Test
  @TestAuthor(githubId = "eMartin0")
  public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
    assertTrue(((IRouletteV2Client)roulettePair.getClient()).isConnected());
  }

  @Test
  @TestAuthor(githubId = "eMartin0")
  public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    assertFalse(client.isConnected());
    client.connect("localhost", port);
    assertTrue(client.isConnected());
  }

  @Test
  @TestAuthor(githubId = "eMartin0")
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "eMartin0")
  public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    client.connect("localhost", port);
    int numberOfStudents = client.getNumberOfStudents();
    assertEquals(0, numberOfStudents);
  }

  @Test
  @TestAuthor(githubId = {"eMartin0", "SoftEng-HEIGVD"})
  public void theServerShouldStillHaveZeroStudentsAtStart() throws IOException {
    assertEquals(0, roulettePair.getClient().getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "SoftEng-HEIGVD")
  public void theServerShouldCountStudents() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    assertEquals(0, client.getNumberOfStudents());
    client.loadStudent("Jean-Pierre Ghangz");
    assertEquals(1, client.getNumberOfStudents());
    client.loadStudent("Johanne Leuenberger");
    assertEquals(2, client.getNumberOfStudents());
    client.loadStudent("Tiffany James");
    assertEquals(3, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "eMartin0")
  public void theServerShouldSendAnErrorResponseWhenRandomIsCalledAndThereIsNoStudent() throws IOException, EmptyStoreException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    exception.expect(EmptyStoreException.class);
    client.pickRandomStudent();
  }

  @Test
  @TestAuthor(githubId = "eMartin0")
  public void theServerShouldSendDataStoreClearedResponse() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.loadStudent("Tiffany James");
    client.loadStudent("Johanne leuenberger");
    client.loadStudent("Jean-Pierre Ghangz");
    client.clearDataStore();
    int numberOfStudents = client.getNumberOfStudents();
    assertEquals(0, numberOfStudents);
  }

  @Test
  @TestAuthor(githubId = "eMartin0")
  public void theServerShouldSendAListOfStudents() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.loadStudent("Tiffany James");
    client.loadStudent("Johanne Leuenberger");
    client.loadStudent("Jean-Pierre Ghangz");
    assertEquals(3, client.listStudents().size());
  }

  @Test
  @TestAuthor(githubId = "eMartin0")
  public void theServerShouldSendTheRightNumberOfNewStudents() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    List<Student> studentsList = new ArrayList<>();
    studentsList.add(new Student("Tiffany James"));
    studentsList.add(new Student("Johanne Leuenberger"));
    client.loadStudents(studentsList);
    assertEquals(2, client.getNumberOfStudentAdded());
    client.loadStudent("Jean-Pierre Ghangz");
    assertEquals(1, client.getNumberOfStudentAdded());
  }

  @Test
  @TestAuthor(githubId = "eMartin0")
  public void theServerShouldBeAbleToReturnTheRightNumberOfCommandsSentForThisConnection() throws IOException{
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
    client.loadStudent("Maëlle Guignard");
    client.getProtocolVersion();
    client.checkSuccessOfCommand();
    client.disconnect();
    assertEquals(4, client.getNumberOfCommands());
  }


}
