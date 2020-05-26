package pdmf.service.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pdmf.model.OperationKey;
import pdmf.model.OperationRec;
import pdmf.model.ProcessKey;
import pdmf.model.ProcessRec;
import pdmf.model.ProductKey;
import pdmf.model.ProductRec;
import pdmf.model.TenantKey;
import pdmf.model.TenantRec;
import pdmf.model.TopicKey;
import pdmf.model.TopicRec;

public class ServiceHelper {

	public static String ensureStringLength(String str, Integer maxLength) {

		if (str == null)
			return str;
		int n = str.length();
		if (n <= maxLength)
			return str;
		return str.substring(0, maxLength);
	}

	public static void validate(String field, String data) {
		if (data == null || data.length() < 1) {
			throw new IllegalArgumentException(field + " has not a valid value" + data);
		}
	}

	public static void validate(String field, Integer data) {
		if (data == null) {
			throw new IllegalArgumentException(field + " has not a valid value" + data);
		}
	}

	public static void validate(String field, Boolean data) {
		if (data == null) {
			throw new IllegalArgumentException(field + " is null" + data);
		}
	}

	public static void validate(TenantRec rec) {

		if (rec == null) {
			throw new IllegalArgumentException("Tenant  is null");
		}
		if (rec.description == null) {
			throw new IllegalArgumentException("Tenant description  is null");
		}
		ServiceHelper.validate(rec.key);
	}

	public static void validate(TenantKey key) {

		if (key == null) {
			throw new IllegalArgumentException("TenantKey  is null");
		}
		if (key.tenantid == null) {
			throw new IllegalArgumentException("TenantKey.name  is null");
		}
	}

	public static void validate(ProductRec rec) {

		if (rec == null) {
			throw new IllegalArgumentException("Product  is null");
		}
		ServiceHelper.validate(rec.key);
	}

	public static void validate(ProductKey key) {

		if (key == null) {
			throw new IllegalArgumentException("ProductKey  is null");
		}
		if (key.tenantid == null) {
			throw new IllegalArgumentException("ProductKey.tenant  is null");
		}
		if (key.version == null) {
			throw new IllegalArgumentException("ProductKey.version  is null");
		}
		if (key.productName == null) {
			throw new IllegalArgumentException("ProductKey.Productname  is not valid");
		}
	}

	public static void validate(TopicRec rec) {

		if (rec == null) {
			throw new IllegalArgumentException("Topic  is null");
		}
		ServiceHelper.validate(rec.key);
	}

	public static void validate(TopicKey key) {

		if (key == null) {
			throw new IllegalArgumentException("TopicKey  is null");
		}
		if (key.tenantid == null) {
			throw new IllegalArgumentException("TopicKey.tenant  is null");
		}
		if (key.version == null) {
			throw new IllegalArgumentException("TopicKey.version  is null");
		}
		if (key.productName == null) {
			throw new IllegalArgumentException("TopicKey.Productname  is not valid");
		}
		if (key.topicName == null) {
			throw new IllegalArgumentException("TopicKey.Topicname  is not valid");
		}
	}

	public static void validate(ProcessRec rec) {

		if (rec == null) {
			throw new IllegalArgumentException("Process  is null");
		}
		ServiceHelper.validate(rec.key);
	}

	public static void validate(ProcessKey key) {

		if (key == null) {
			throw new IllegalArgumentException("ProcessKey  is null");
		}
		if (key.tenantid == null) {
			throw new IllegalArgumentException("ProcessKey.tenant  is null");
		}
		if (key.version == null) {
			throw new IllegalArgumentException("ProcessKey.version  is null");
		}
		if (key.productName == null) {
			throw new IllegalArgumentException("ProcessKey.Productname  is not valid");
		}
		if (key.topicName == null) {
			throw new IllegalArgumentException("ProcessKey.Topicname  is not valid");
		}
		if (key.processName == null) {
			throw new IllegalArgumentException("ProcessKey.Processname  is not valid");
		}
		if (key.processSeq == null) {
			throw new IllegalArgumentException("ProcessKey.Sequence  is not valid");
		}
	}

	public static void validate(OperationRec rec) {

		if (rec == null) {
			throw new IllegalArgumentException("Operation  is null");
		}
		ServiceHelper.validate(rec.key);
	}

	public static void validate(OperationKey key) {

		if (key == null) {
			throw new IllegalArgumentException("Operation  is null");
		}
		if (key.tenantid == null) {
			throw new IllegalArgumentException("OperationKey.tenant  is null");
		}
		if (key.version == null) {
			throw new IllegalArgumentException("OperationKey.version  is null");
		}
		if (key.productName == null) {
			throw new IllegalArgumentException("OperationKey.Productname  is not valid");
		}
		if (key.topicName == null) {
			throw new IllegalArgumentException("OperationKey.Topicname  is not valid");
		}
		if (key.processName == null) {
			throw new IllegalArgumentException("OperationKey.Processname  is not valid");
		}
		if (key.sequence == null) {
			throw new IllegalArgumentException("OperationKey.Sequence  is not valid");
		}
		if (key.operationName == null) {
			throw new IllegalArgumentException("OperationKey.OperationName  is not valid");
		}
		if (key.operationSequence == null) {
			throw new IllegalArgumentException("OperationKey.OperationSequence  is not valid");
		}
	}

	public static void validate(List<String> key) {

		if (key == null) {
			throw new IllegalArgumentException("List  is null");
		}
	}

	private static Map<String, String> cache = new HashMap<>();

	/**
	 * 
	 * @param filename NO extension
	 * @return
	 */

	public static String getSQL(String filename) {

		ServiceHelper.validate("Filename", filename);

		if (!cache.containsKey(filename)) {

			try (InputStream is = ServiceHelper.class.getClassLoader().getResourceAsStream(filename + ".txt");
					InputStreamReader isReader = new InputStreamReader(is);
					BufferedReader reader = new BufferedReader(isReader);) {

				StringBuffer sb = new StringBuffer();
				String str;
				while ((str = reader.readLine()) != null) {
					String tmp = str.trim();
					tmp = " " + tmp + " ";
					sb.append(tmp);
				}
				String theSQL = sb.toString();
				cache.put(filename, theSQL);
			} catch (IOException e) {
				return null;
			}
		}
		return cache.get(filename);
	}

}
