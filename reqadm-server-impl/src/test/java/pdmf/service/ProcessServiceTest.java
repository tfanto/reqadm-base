package pdmf.service;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pdmf.model.ProcessKey;
import pdmf.model.ProcessRec;
import pdmf.sys.Db;
import pdmf.sys.RecordChangedByAnotherUser;

public class ProcessServiceTest extends TestHelper {

	@BeforeClass
	public static void beforeClass() {
		Db.setupDatabasePool();
	}

	@AfterClass
	public static void afterClass() {
		Db.stopDatabasePool();
	}

	@Before
	public void before() {
		Db.clear();
		setupProcessTestData1();
		setupProcessTestData2();
	}

	ProcessService processService = new ProcessService();

	Integer version = 1;
	String productName = "ois";
	String topicName = "customer";
	String processName = "crud";
	Integer seq = 0;
	String operationNameADD = "add";
	Integer operationSeq = 0;

	@Test
	public void getList() {
		List<ProcessRec> list = processService.list(tenant, version, productName, topicName);
		Assert.assertEquals(1, list.size());
	}

	@Test
	public void getList_Fail() {
		List<ProcessRec> list = processService.list(tenant, version, productName, topicName);
		Assert.assertEquals(1, list.size());
	}

	@Test
	public void getList_Fail2() {
		List<ProcessRec> list = processService.list(tenant, version, productName + "NO", topicName);
		Assert.assertEquals(0, list.size());
	}

	@Test
	public void get() {
		ProcessKey key = new ProcessKey(tenant, version, productName, topicName, processName, seq);
		processService.remove(key, "test");
		ProcessRec rec = processService.get(key);
		Assert.assertNotNull(rec.dltusr);
	}

	@Test
	public void getFail() {
		ProcessKey key = new ProcessKey(tenant, version, productName, topicName, processName, seq);
		ProcessRec rec = processService.get(key);
		Assert.assertEquals("customer", rec.key.topicName);
	}

	@Test
	public void exist() {
		ProcessKey key = new ProcessKey(tenant, version, productName, topicName, processName, seq);
		Boolean exists = processService.exists(key);
		Assert.assertTrue(exists);
	}

	@Test
	public void existFail() {

		ProcessKey key = new ProcessKey(tenant, version, productName, topicName, processName, seq);

		processService.remove(key, "test");

		Boolean exists = processService.isDeleteMarked(key);
		Assert.assertTrue(exists);
	}

	@Test
	public void storeNew() {
		ProcessKey key = new ProcessKey(tenant, version, productName, topicName, processName, seq);

		processService.remove(key, "test");

		ProcessRec rec = new ProcessRec(key, "aNewRecord", null, null);
		rec.shortdescr = "kort";
		processService.store(rec, "test");
		ProcessRec fetched = processService.get(key);
		Assert.assertNotNull(fetched);

	}

	@Test
	public void storeExisting() {

		ProcessKey key = new ProcessKey(tenant, version, productName, topicName, processName, seq);
		ProcessRec rec = processService.get(key);

		rec.shortdescr = "kort";
		rec.description = "aNewRecord2";
		processService.store(rec, "test");
		ProcessRec fetched = processService.get(key);
		Assert.assertEquals("aNewRecord2", fetched.description);
		fetched.description = "CHANGED";
		rec.shortdescr = "again";
		processService.store(fetched, "test");
		fetched = processService.get(key);

		Assert.assertNotNull(fetched);
		Assert.assertEquals("CHANGED", fetched.description);

	}

	@Test
	public void removeExisting() {

		ProcessKey key = new ProcessKey(tenant, version, productName, topicName, processName, seq);
		processService.remove(key, "test");
		Boolean deletemarked = processService.isDeleteMarked(key);
		Assert.assertTrue(deletemarked);
	}

	@Test
	public void removeNonExisting() {
		ProcessKey key = new ProcessKey(tenant, version, productName, topicName, processName, seq);
		processService.remove(key, "test");
		ProcessRec lookup = new ProcessRec(key, null, null, null);
		Boolean exists = processService.isDeleteMarked(lookup.key);
		Assert.assertTrue(exists);
	}

	@Test(expected = RecordChangedByAnotherUser.class)
	public void updatedByAnotherUser() {
		ProcessKey key = new ProcessKey(tenant, version, productName, topicName, processName, seq);

		ProcessRec fetched1 = processService.get(key);
		fetched1.description = "CHANGED1";

		ProcessRec fetched2 = processService.get(key);
		fetched2.description = "CHANGED BY THE FAST ONE";
		processService.store(fetched2, "test");

		fetched1.description = "CHANGED BY THE SLOW ONE";
		processService.store(fetched1, "test");

	}

}
