package pdmf.service.support;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pdmf.model.ProductKey;
import pdmf.service.TestHelper;
import pdmf.sys.Db;

public class ExportSupportTest extends TestHelper {

	ExportSupport exportSupport = new ExportSupport();

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
		setupOperationTestData1();
		setupOperationTestData2();
	}

	Integer version = 1;
	String productName = "ois";
	String topicName = "customer";
	String processName = "crud";
	Integer seq = 0;
	String operationNameADD = "add";
	Integer operationSeq = 0;

	@Test
	public void getList() {

		ProductKey key = new ProductKey(tenant, version, productName);
		String aXMLString = exportSupport.convertToXML(key);
		Assert.assertNotNull(aXMLString);

	}

}
