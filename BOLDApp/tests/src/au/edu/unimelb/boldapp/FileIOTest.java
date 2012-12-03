package au.edu.unimelb.boldapp;

import java.io.File;
import java.util.Date;
import java.util.UUID;
import java.util.List;

import android.util.Log;

import org.apache.commons.io.FileUtils;

import junit.framework.TestCase;

public class FileIOTest extends TestCase {

	public void testGetAppRootPath() throws Exception {
		assertEquals(new File("/mnt/sdcard/bold"), FileIO.getAppRootPath());
	}

	public void testGetUsersPath() throws Exception {
		assertEquals(new File("/mnt/sdcard/bold/users"),
				FileIO.getUsersPath());
	}

	public void testGetImagesPath() throws Exception {
		assertEquals(new File("/mnt/sdcard/bold/images"),
				FileIO.getImagesPath());
	}

	public void testGetRecordingsPath() throws Exception {
		assertEquals(new File("/mnt/sdcard/bold/recordings"),
				FileIO.getRecordingsPath());
	}

	public void testWriteRead1() throws Exception {
		FileIO.write("testdir/test1/test1", "hallo");
		assertEquals("hallo", FileIO.read("/mnt/sdcard/bold/testdir/test1/test1"));
	}

	public void testWriteRead2() throws Exception {
		FileIO.write("/mnt/sdcard/bold/testdir/test1/", "okies");
		assertEquals("okies", FileIO.read("testdir/test1/"));
	}

	public void testWriteRead3() throws Exception {
		FileIO.write("testdir/test3", "once upon\n a time\n");
		assertEquals("once upon\n a time\n", FileIO.read("testdir/test3"));
	}

	public void testWriteRead4() throws Exception {
		FileIO.write("testdir/test4", "once upon\n a time");
		assertTrue(!"once upon\n a time\n"
				.equals(FileIO.read("testdir/test4")));
	}

	public void testWriteRead5() throws Exception {
		FileIO.write(
				new File(FileIO.getAppRootPath(), "testdir/test1/test1"),
				"hallo");
		assertEquals("hallo", FileIO.read(
				new File(FileIO.getAppRootPath(), "testdir/test1/test1")));
	}

	public void testWriteAndReadUsers() throws Exception {
		// Writes two users, and then reads them back.

		User user = new User(UUID.randomUUID(), "Test User");
		FileIO.writeUser(user);

		User user2 = new User(UUID.randomUUID(), "Test Üser 2");
		FileIO.writeUser(user2);

		// NOTE: ASSUMING readUsers() RETURNS THE LIST IN REVERSE CHRONOLOGICAL
		// ORDER
		List<User> users = FileIO.readUsers();
		assertEquals(user.getName(), users.get(1).getName());
		assertEquals(user.getUUID(), users.get(1).getUUID());
		assertEquals(user2.getName(), users.get(0).getName());
		assertEquals(user2.getUUID(), users.get(0).getUUID());

		// Cleanup these test user directories.
		FileUtils.deleteDirectory(new File(FileIO.getUsersPath(),
				user.getUUID().toString()));
		FileUtils.deleteDirectory(new File(FileIO.getUsersPath(),
				user2.getUUID().toString()));
	}

	public void testWriteAndReadRecordingMeta() throws Exception {

		Recording recording = new Recording(
				UUID.randomUUID(), UUID.randomUUID(), "Test",
				new Date());

		Recording recording2 = new Recording(
				UUID.randomUUID(), UUID.randomUUID(), "Test",
				new Date(), UUID.randomUUID());

		FileIO.writeRecordingMeta(recording);
		FileIO.writeRecordingMeta(recording2);

		List<Recording> recordings = FileIO.readRecordingsMeta();
		Log.i("FileIO", " " + recordings.size());

		assertEquals(recording.getUUID(), recordings.get(1).getUUID());
		assertEquals(recording.getCreatorUUID(),
				recordings.get(1).getCreatorUUID());
		assertEquals(recording.getName(), recordings.get(1).getName());
		assertEquals(recording.getDate(), recordings.get(1).getDate());
		assertEquals(null, recordings.get(1).getOriginalUUID());

		assertEquals(recording2.getUUID(), recordings.get(0).getUUID());
		assertEquals(recording2.getCreatorUUID(), recordings.get(0).getCreatorUUID());
		assertEquals(recording2.getName(), recordings.get(0).getName());
		assertEquals(recording2.getDate(), recordings.get(0).getDate());
		assertEquals(recording2.getOriginalUUID(),
				recordings.get(0).getOriginalUUID());

		// Do some cleanup
		assertTrue(new File(FileIO.getRecordingsPath(),
				recording.getUUID() + ".json").delete());
		assertTrue(new File(FileIO.getRecordingsPath(),
				recording2.getUUID() + ".json").delete());
	}

	@Override
	public void tearDown() throws Exception {

		FileUtils.deleteDirectory(new File(FileIO.getAppRootPath(),
				"testdir"));
	}

}