package pdmf.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pdmf.model.ProductKey;
import pdmf.model.ProductRec;
import pdmf.sys.Db;
import pdmf.sys.RecordChangedByAnotherUser;

public class ProductServiceTest extends TestHelper {

	@BeforeClass
	public static void beforeClass() {
		Db.setupDatabasePool();
		Db.clear();
		// setupOperationTestData1();
		// setupOperationTestData2();
	}

	@AfterClass
	public static void afterClass() {
		Db.stopDatabasePool();
	}

	@Before
	public void before() {
		Db.clear();
		// setupOperationTestData1();
		// setupOperationTestData2();
		setupDataForProductTests();
	}

	@After
	public void after() {
	}

	private ProductService productService = new ProductService();

	Integer version = 1;
	String productName = "ois";
	String topicName = "customer";
	String processName = "crud";
	Integer seq = 0;
	String operationNameADD = "add";
	Integer operationSeq = 0;

	@Test
	public void list() {
		List<String> list = productService.list(tenant);
		Assert.assertNotNull(list);
	}

	@Test
	public void listByName() {
		List<ProductRec> list = productService.list(tenant, "ois");
		Assert.assertNotNull(list);
	}

	@Test
	public void listByNameFAIL() {
		List<ProductRec> list = productService.list(tenant, "oisNOWAY");
		Assert.assertNotNull(list);
	}

	@Test
	public void listByNameAndVersion() {
		ProductKey key = new ProductKey(tenant, 1, "ois");
		List<ProductRec> list = productService.list(key);
		Assert.assertNotNull(list);
	}

	@Test
	public void listByNameAndVersionFAIL() {
		ProductKey key = new ProductKey(tenant, 134, "ois");
		List<ProductRec> list = productService.list(key);
		Assert.assertNotNull(list);
	}

	@Test
	public void getList() {
		List<ProductRec> recs = productService.list(tenant, version);
		Assert.assertEquals(2, recs.size());
	}

	@Test
	public void getList_Fail() {
		List<ProductRec> recs = productService.list(tenant, 33);
		Assert.assertEquals(0, recs.size());
	}

	@Test
	public void get() {
		ProductKey key = new ProductKey(tenant, version, "ois");
		ProductRec rec = productService.get(key);
		Assert.assertNotNull(rec);
	}

	@Test
	public void getFail() {
		ProductKey key = new ProductKey(tenant, 345, "ois");
		ProductRec rec = productService.get(key);
		Assert.assertEquals(null, rec);
	}

	@Test
	public void exist() {
		ProductKey key = new ProductKey(tenant, 1, productName);

		Boolean exists = productService.exists(key);
		Assert.assertTrue(exists);
	}

	@Test
	public void existFail() {
		ProductKey key = new ProductKey(tenant, 42, productName);

		productService.remove(key, "test");
		Boolean exists = productService.exists(key);
		Assert.assertFalse(exists);
	}

	@Test
	public void storeNew() {

		ProductKey key = new ProductKey(tenant, version, productName + "NEW");
		ProductRec rec = new ProductRec(key, "aNewRecord", null, null);
		rec.shortdescr = "hepp";
		productService.store(rec, "test");
		ProductRec fetched = productService.get(key);
		Assert.assertNotNull(fetched);
	}

	@Test
	public void storeExisting() {
		ProductKey key = new ProductKey(tenant, version, productName);

		ProductRec fetched = productService.get(key);
		Assert.assertEquals("ois longdescription", fetched.description);
		fetched.description = "CHANGED";
		productService.store(fetched, "test");
		fetched = productService.get(key);
		Assert.assertNotNull(fetched);
		Assert.assertEquals("CHANGED", fetched.description);

	}

	@Test
	public void removeExisting() {
		ProductKey key = new ProductKey(tenant, version, productName);
		ProductRec rec = new ProductRec(key, "aNewRecord2", null, null);
		rec.chgnbr = 0;
		productService.store(rec, "test");

		ProductRec fetched = productService.get(key);
		Assert.assertEquals("aNewRecord2", fetched.description);
		productService.remove(key, "test");

		ProductRec deleteMarked = productService.get(key);
		Assert.assertTrue(deleteMarked.dltusr != null);
	}

	@Test
	public void removeNonExisting() {
		ProductKey key = new ProductKey(tenant, 171, productName);

		productService.remove(key, "test");
		Boolean exists = productService.exists(key);
		Assert.assertFalse(exists);
	}

	@Test
	public void simpleQuery() {

		List<Map<String, Object>> rs = Db.simpleQuery("select * from product");
		assertNotNull(rs);

	}

	@Test(expected = RecordChangedByAnotherUser.class)
	public void updatedByAnotherUser() {
		ProductKey key = new ProductKey(tenant, version, productName);

		ProductRec fetched1 = productService.get(key);
		ProductRec fetched2 = productService.get(key);
		productService.store(fetched2, "test");
		productService.store(fetched1, "test");

	}

	@Test
	public void versionTest() {

		ProductKey key = new ProductKey(tenant, version, productName);

		ProductRec rec = productService.get(key);
		Boolean exists = productService.exists(key);
		Assert.assertTrue(exists);

		int newVersion = version + 5;
		productService.createNewVersion(tenant, version, newVersion, productName, "test");
		productService.store(rec, "test");

	}

}
