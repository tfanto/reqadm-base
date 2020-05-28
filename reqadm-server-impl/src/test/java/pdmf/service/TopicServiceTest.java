package pdmf.service;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pdmf.model.TopicKey;
import pdmf.model.TopicRec;
import pdmf.sys.Db;
import pdmf.sys.RecordChangedByAnotherUser;

public class TopicServiceTest extends TestHelper {

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
		setupTopicTestData1();
		setupTopicTestData2();
	}

	TopicService topicService = new TopicService();

	Integer version = 1;
	String productName = "ois";
	String topicName = "customer";
	String processName = "crud";
	Integer seq = 0;
	String operationNameADD = "add";
	Integer operationSeq = 0;

	@Test
	public void getList() {
		List<TopicRec> list = topicService.list(tenant, version, productName);
		Assert.assertEquals(4, list.size());
	}

	@Test
	public void getList_Fail() {
		List<TopicRec> list = topicService.list(tenant, 42, productName);
		Assert.assertEquals(0, list.size());
	}

	@Test
	public void getList_Fail2() {
		List<TopicRec> list = topicService.list(tenant, version, productName + "NO");
		Assert.assertEquals(0, list.size());
	}

	@Test
	public void get() {
		TopicKey key = new TopicKey(tenant, version, productName, topicName);
		topicService.remove(key, "test");
		TopicRec rec = topicService.get(key);
		Assert.assertTrue(rec.dltusr != null);
	}

	@Test
	public void getFail() {
		TopicKey key = new TopicKey(tenant, version, productName, topicName);
		TopicRec rec = topicService.get(key);
		Assert.assertEquals("customer", rec.key.topicName);
	}

	@Test
	public void exist() {
		TopicKey key = new TopicKey(tenant, version, productName, topicName);
		Boolean exists = topicService.exists(key);
		Assert.assertTrue(exists);
	}

	@Test
	public void existFail() {

		TopicKey key = new TopicKey(tenant, version, productName, topicName);
		topicService.remove(key, "test");

		Boolean ok = topicService.isDeleteMarked(key);
		Assert.assertTrue(ok);
	}

	@Test
	public void storeNew() {

		TopicKey key = new TopicKey(tenant, version, productName, topicName);
		topicService.remove(key, "test");

		TopicRec rec = new TopicRec(key, "aNewRecord", null, null);
		rec.shortdescr = "kort";
		topicService.store(rec, "test");
		TopicRec fetched = topicService.get(key);
		Assert.assertNotNull(fetched);

	}

	@Test
	public void storeExisting() {
		TopicKey key = new TopicKey(tenant, version, productName, topicName);

		TopicRec fetched = topicService.get(key);
		fetched.description = "CHANGED";
		topicService.store(fetched, "test");
		fetched = topicService.get(key);

		Assert.assertNotNull(fetched);
		Assert.assertEquals("CHANGED", fetched.description);

	}

	@Test
	public void removeExisting() {

		TopicKey key = new TopicKey(tenant, version, productName, topicName);
		topicService.remove(key, "test");


		Boolean ok = topicService.isDeleteMarked(key);
		Assert.assertTrue(ok);
	}

	@Test
	public void removeNonExisting() {
		TopicKey key = new TopicKey(tenant, version, productName, topicName);
		topicService.remove(key, "test");
		Boolean ok = topicService.isDeleteMarked(key);
		Assert.assertTrue(ok);
	}

	@Test(expected = RecordChangedByAnotherUser.class)
	public void updatedByAnotherUser() {

		TopicKey key = new TopicKey(tenant, version, productName, topicName);
		TopicRec fetched1 = topicService.get(key);

		fetched1.description = "CHANGED1";

		TopicRec fetched2 = topicService.get(key);
		fetched2.description = "CHANGED BY THE FAST ONE";
		topicService.store(fetched2, "test");

		fetched1.description = "CHANGED BY THE SLOW ONE";
		topicService.store(fetched1, "test");

	}

}
