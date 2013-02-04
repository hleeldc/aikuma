package au.edu.unimelb.boldapp;

import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
		File testFile = 
				new File(FileIO.getAppRootPath(), "testdir/test1/test1");
		FileIO.write(testFile, "hallo");
		assertEquals("hallo", FileIO.read("/mnt/sdcard/bold/testdir/test1/test1"));
	}

	public void testWriteRead2() throws Exception {
		File testFile =
			new File(FileIO.getAppRootPath(), "/testdir/test1/");
		FileIO.write(testFile, "okies");
		assertEquals("okies", FileIO.read("testdir/test1/"));
	}

	public void testWriteRead3() throws Exception {
		File testFile =
			new File(FileIO.getAppRootPath(), "testdir/test3");
		FileIO.write(testFile, "once upon\n a time\n");
		assertEquals("once upon\n a time\n", FileIO.read("testdir/test3"));
	}

	public void testWriteRead4() throws Exception {
		File testFile =
			new File(FileIO.getAppRootPath(), "testdir/test4");
		FileIO.write(testFile, "once úpon\n a tîme");
		assertTrue(!"once úpon\n a tîme\n"
				.equals(FileIO.read("testdir/test4")));
	}

	public void testWriteRead5() throws Exception {
		FileIO.write(
				new File(FileIO.getAppRootPath(), "testdir/test1/test1"),
				"hallo");
		assertEquals("hallo", FileIO.read(
				new File(FileIO.getAppRootPath(), "testdir/test1/test1")));
	}

	public void testEncodeLanguage() throws Exception {
		Language language = new Language("English", "eng");
		JSONObject encodedLanguage = FileIO.encodeLanguage(language);
		assertEquals("{\"code\":\"eng\",\"name\":\"English\"}",
				encodedLanguage.toString());
	}

	public void testEncodeLanguages() throws Exception {
		Language l1 = new Language("Alekano", "gah");
		Language l2 = new Language("Usarufa", "usa");
		List<Language> languages = new ArrayList<Language>();
		languages.add(l1);
		languages.add(l2);
		JSONArray encodedLanguages = FileIO.encodeLanguages(languages);
		assertEquals(
				"[{\"code\":\"gah\",\"name\":\"Alekano\"},{\"code\":\"usa\",\"name\":\"Usarufa\"}]",
				encodedLanguages.toString());
	}

	public void testEncodeUser() throws Exception {
		Language l1 = new Language("Alekano", "gah");
		Language l2 = new Language("Usarufa", "usa");
		List<Language> languages = new ArrayList<Language>();
		languages.add(l1);
		languages.add(l2);
		User user = new User(UUID.randomUUID(), "TestUser", languages);
		assertEquals(
				"{\"languages\":[{\"code\":\"gah\",\"name\":\"Alekano\"},{\"code\":\"usa\",\"name\":\"Usarufa\"}],\"uuid\":\""
				+ user.getUUID() + "\",\"name\":\"TestUser\"}",
				FileIO.encodeUser(user).toString());
	}

	public void testReadUser1() throws Exception {
		UUID uuid = UUID.randomUUID();
		String jsonStr =
			"{\"uuid\":\"" + uuid + "\",\"name\":\"TestReadUser1\"}";
		File file = new File(FileIO.getUsersPath(), uuid.toString() +
				"/metadata.json");
		FileIO.write(file, jsonStr);
		User user = FileIO.readUser(uuid.toString());
		assertEquals("TestReadUser1", user.getName());
		assertEquals(uuid, user.getUUID());
		List<Language> languages = new ArrayList<Language>();
		assertEquals(languages, user.getLanguages());
		FileUtils.deleteDirectory(file.getParentFile());
	}

	public void testReadUser2() throws Exception {
		UUID uuid = UUID.randomUUID();
		String jsonStr = "{\"languages\":[{\"code\":\"gah\",\"name\":\"Alekano"
			+ "\"},{\"code\":\"usa\",\"name\":\"Usarufa\"}],"
			+ "\"name\":\"TestReadUser1\"}";
		File file = new File(FileIO.getUsersPath(), uuid.toString() +
				"/metadata.json");
		FileIO.write(file, jsonStr);
		try {
			User user = FileIO.readUser(uuid.toString());
		} catch (IOException e) {
			assertEquals("No UUID in the JSON file.", e.getMessage());
		}
		FileUtils.deleteDirectory(file.getParentFile());

	}

	public void testReadUser3() throws Exception {
		UUID uuid = UUID.randomUUID();
		String jsonStr = "{\"languages\":[{\"code\":\"gah\",\"name\":\"Alekano"
			+ "\"},{\"code\":\"usa\",\"name\":\"Usarufa\"}],"
			+ "\"uuid\":\"" + uuid + "\"}";
		File file = new File(FileIO.getUsersPath(), uuid.toString() +
				"/metadata.json");
		FileIO.write(file, jsonStr);
		try {
			User user = FileIO.readUser(uuid.toString());
		} catch (IOException e) {
			assertEquals("No user name in the JSON file.", e.getMessage());
		}
		FileUtils.deleteDirectory(file.getParentFile());
	}

	public void testReadUser4() throws Exception {
		UUID uuid = UUID.randomUUID();
		String jsonStr = "\"languages\":[{\"code\":\"gah\",\"name\":\"Alekano"
			+ "\"},{\"code\":\"usa\",\"name\":\"Usarufa\"}],"
			+ "\"uuid\":\"" + uuid + "\"}";
		File file = new File(FileIO.getUsersPath(), uuid.toString() +
				"/metadata.json");
		FileIO.write(file, jsonStr);
		boolean caught = false;
		try {
			User user = FileIO.readUser(uuid.toString());
		} catch (IOException e) {
			caught = true;
		}
		assertTrue(caught);
		FileUtils.deleteDirectory(file.getParentFile());

	}

	public void testWriteAndReadUsers() throws Exception {
		Language l1 = new Language("Alekano", "gah");
		List<Language> languages = new ArrayList<Language>();
		languages.add(l1);
		// Writes two users, and then reads them back.

		User user = new User(UUID.randomUUID(), "Test User", languages);
		FileIO.writeUser(user);

		User user2 = new User(UUID.randomUUID(), "Test Üser 2", languages);
		FileIO.writeUser(user2);

		// NOTE: ASSUMING readUsers() RETURNS THE LIST IN REVERSE CHRONOLOGICAL
		// ORDER.
		List<User> users = FileIO.readUsers();

		Log.i("FileIOTest", user.getLanguages().get(0).getCode());
		Log.i("FileIOTest", users.get(1).getLanguages().get(0).getCode());

		assertEquals(user.getName(), users.get(1).getName());
		assertEquals(user.getUUID(), users.get(1).getUUID());
		assertEquals(user.getLanguages().get(0).getName(),
				users.get(1).getLanguages().get(0).getName());
		assertEquals(user.getLanguages().get(0).getCode(),
				users.get(1).getLanguages().get(0).getCode());
		assertEquals(user2.getName(), users.get(0).getName());
		assertEquals(user2.getUUID(), users.get(0).getUUID());
		assertEquals(user.getLanguages().get(0).getName(),
				users.get(0).getLanguages().get(0).getName());
		assertEquals(user.getLanguages().get(0).getCode(),
				users.get(0).getLanguages().get(0).getCode());


		//If there are other users, this won't work.
		// Cleanup these test user directories.
		FileUtils.deleteDirectory(new File(FileIO.getUsersPath(),
				user.getUUID().toString()));
		FileUtils.deleteDirectory(new File(FileIO.getUsersPath(),
				user2.getUUID().toString()));
	}

	public void testReadRecording1() throws Exception {
		UUID uuid = UUID.randomUUID();
		UUID creatorUUID = UUID.randomUUID();
		UUID originalUUID = UUID.randomUUID();
		String date_string = new StandardDateFormat().format(new Date());
		String jsonStr = "{\"uuid\":\"" + uuid + "\", \"creator_uuid\":\"" +
				creatorUUID + "\", " + "\"recording_name\":\"TestRecording\", "
				+ "\"date_string\":\"" + date_string + "\", \"language_name\":"
				+ "\"Usarufa\", \"language_code\":\"usa\", \"original_uuid\":\""
				+ originalUUID + "\"}";
		File file = new File(FileIO.getRecordingsPath(), uuid.toString() +
				".json");
		FileIO.write(file, jsonStr);
		Recording recording = FileIO.readRecording(file);
		assertEquals(uuid, recording.getUUID());
		assertEquals(creatorUUID, recording.getCreatorUUID());
		assertEquals(originalUUID, recording.getOriginalUUID());
		assertEquals(date_string, 
				new StandardDateFormat().format(recording.getDate()));
		assertEquals("TestRecording", recording.getName());
		assertEquals("Usarufa", recording.getLanguage().getName());
		assertEquals("usa", recording.getLanguage().getCode());

		assertTrue(file.delete());
	}

	public void testReadRecording2() throws Exception {
		UUID uuid = UUID.randomUUID();
		UUID creatorUUID = UUID.randomUUID();
		UUID originalUUID = UUID.randomUUID();
		String date_string = new StandardDateFormat().format(new Date());
		String jsonStr = "\"uuid\":\"" + uuid + "\", \"creator_uuid\":\"" +
				creatorUUID + "\", " + "\"recording_name\":\"TestRecording\", "
				+ "\"date_string\":\"" + date_string + "\", \"language_name\":"
				+ "\"Usarufa\", \"language_code\":\"usa\", \"original_uuid\":\""
				+ originalUUID + "\"}";
		File file = new File(FileIO.getRecordingsPath(), uuid.toString() +
				".json");
		FileIO.write(file, jsonStr);
		boolean caught = false;
		try {
			Recording recording = FileIO.readRecording(file);
		} catch (Exception e) {
			caught = true;
		}
		assertTrue(caught);
		assertTrue(file.delete());
	}

	public void testReadRecording3() throws Exception {
		UUID uuid = UUID.randomUUID();
		UUID creatorUUID = UUID.randomUUID();
		UUID originalUUID = UUID.randomUUID();
		String date_string = new StandardDateFormat().format(new Date());
		String jsonStr = "{\"uuid\":\"" + uuid + "\", \"SOME_RANDOM_KEY\":\"" +
				creatorUUID + "\", " + "\"recording_name\":\"TestRecording\", "
				+ "\"date_string\":\"" + date_string + "\", \"language_name\":"
				+ "\"Usarufa\", \"language_code\":\"usa\", \"original_uuid\":\""
				+ originalUUID + "\"}";
		File file = new File(FileIO.getRecordingsPath(), uuid.toString() +
				".json");
		FileIO.write(file, jsonStr);
		Recording recording = FileIO.readRecording(file);
		assertEquals(uuid, recording.getUUID());
		assertEquals(null, recording.getCreatorUUID());
		assertEquals(originalUUID, recording.getOriginalUUID());
		assertEquals(date_string, 
				new StandardDateFormat().format(recording.getDate()));
		assertEquals("TestRecording", recording.getName());
		assertEquals("Usarufa", recording.getLanguage().getName());
		assertEquals("usa", recording.getLanguage().getCode());

		assertTrue(file.delete());
	}

	/*
	public void testWriteAndReadRecording() throws Exception {
		Language language = new Language("English", "eng");
		Recording recording = new Recording(
				UUID.randomUUID(), UUID.randomUUID(), "FileIOTestRecording",
				new Date(), language);

		FileIO.writeRecording(recording);

		Recording readRecording = FileIO.readRecording(recording.getUUID());

		Log.i("FileIOTest", recording.getLanguage().toString());
		Log.i("FileIOTest", readRecording.getLanguage().toString());

		assertEquals(recording.getUUID(), readRecording.getUUID());
		assertEquals(recording.getCreatorUUID(),
				readRecording.getCreatorUUID());
		assertEquals(recording.getName(), readRecording.getName());
		assertEquals(recording.getDate(), readRecording.getDate());
		assertEquals(recording.getOriginalUUID(), readRecording.getOriginalUUID());
		assertEquals(recording.getLanguage().getName(),
				readRecording.getLanguage().getName());
		assertEquals(recording.getLanguage().getCode(),
				readRecording.getLanguage().getCode());

		// Do some cleanup
		assertTrue(new File(FileIO.getRecordingsPath(),
				recording.getUUID() + ".json").delete());
	}

	public void testWriteAndReadRecording2() throws Exception {
		Language language = new Language("English", "eng");

		Recording recording = new Recording(
				UUID.randomUUID(), UUID.randomUUID(), "Test",
				new Date(), language);

		Recording recording2 = new Recording(
				UUID.randomUUID(), UUID.randomUUID(), "Test",
				new Date(), language, UUID.randomUUID());

		FileIO.writeRecording(recording);
		FileIO.writeRecording(recording2);

		List<Recording> recordings = FileIO.readRecordings();
		Log.i("FileIO", " " + recordings.size());

		assertEquals(recording.getUUID(), recordings.get(1).getUUID());
		assertEquals(recording.getCreatorUUID(),
				recordings.get(1).getCreatorUUID());
		assertEquals(recording.getName(), recordings.get(1).getName());
		assertEquals(recording.getDate(), recordings.get(1).getDate());
		assertEquals(null, recordings.get(1).getOriginalUUID());
		assertEquals(recording2.getLanguage().getName(),
				recordings.get(1).getLanguage().getName());
		assertEquals(recording2.getLanguage().getCode(),
				recordings.get(1).getLanguage().getCode());

		assertEquals(recording2.getUUID(), recordings.get(0).getUUID());
		assertEquals(recording2.getCreatorUUID(), recordings.get(0).getCreatorUUID());
		assertEquals(recording2.getName(), recordings.get(0).getName());
		assertEquals(recording2.getDate(), recordings.get(0).getDate());
		assertEquals(recording2.getOriginalUUID(),
				recordings.get(0).getOriginalUUID());
		assertEquals(recording2.getLanguage().getName(),
				recordings.get(0).getLanguage().getName());
		assertEquals(recording2.getLanguage().getCode(),
				recordings.get(0).getLanguage().getCode());

		// Do some cleanup
		assertTrue(new File(FileIO.getRecordingsPath(),
				recording.getUUID() + ".json").delete());
		assertTrue(new File(FileIO.getRecordingsPath(),
				recording2.getUUID() + ".json").delete());
	}
	*/

	@Override
	public void tearDown() throws Exception {

		FileUtils.deleteDirectory(new File(FileIO.getAppRootPath(),
				"testdir"));
	}
}
