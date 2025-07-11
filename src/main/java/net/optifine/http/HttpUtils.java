package net.optifine.http;

import net.minecraft.client.Minecraft;
import net.minecraft.src.Config;
import net.optifine.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtils {
	public static final String SERVER_URL = "http://s.optifine.net";
	public static final String POST_URL = "http://optifine.net";
	private static String playerItemsUrl = null;

	public static byte[] get(String urlStr) throws IOException {
		HttpURLConnection httpurlconnection = null;
		byte[] abyte1;

		try {
			URL url = new URI(urlStr).toURL();
			httpurlconnection = (HttpURLConnection) url.openConnection(Minecraft.getMinecraft().getProxy());
			httpurlconnection.setDoInput(true);
			httpurlconnection.setDoOutput(false);
			httpurlconnection.connect();

			if (httpurlconnection.getResponseCode() / 100 != 2) {
				if (httpurlconnection.getErrorStream() != null) {
					Config.readAll(httpurlconnection.getErrorStream());
				}

				throw new IOException("HTTP response: " + httpurlconnection.getResponseCode());
			}

			InputStream inputstream = httpurlconnection.getInputStream();
			byte[] abyte = new byte[httpurlconnection.getContentLength()];
			int i = 0;

			while (true) {
				int j = inputstream.read(abyte, i, abyte.length - i);

				if (j < 0) {
					throw new IOException("Input stream closed: " + urlStr);
				}

				i += j;

				if (i >= abyte.length) {
					break;
				}
			}

			abyte1 = abyte;
		} catch (URISyntaxException exception) {
			throw new RuntimeException(exception);
		} finally {
			if (httpurlconnection != null) {
				httpurlconnection.disconnect();
			}
		}

		return abyte1;
	}

	public static String post(String urlStr, Map headers, byte[] content) throws IOException, URISyntaxException {
		HttpURLConnection httpurlconnection = null;
		String s3;

		try {
			URL url = new URI(urlStr).toURL();
			httpurlconnection = (HttpURLConnection) url.openConnection(Minecraft.getMinecraft().getProxy());
			httpurlconnection.setRequestMethod("POST");

			if (headers != null) {
				for (Object s : headers.keySet()) {
					String s1 = "" + headers.get(s);
					httpurlconnection.setRequestProperty((String) s, s1);
				}
			}

			httpurlconnection.setRequestProperty("Content-Type", "text/plain");
			httpurlconnection.setRequestProperty("Content-Length", "" + content.length);
			httpurlconnection.setRequestProperty("Content-Language", "en-US");
			httpurlconnection.setUseCaches(false);
			httpurlconnection.setDoInput(true);
			httpurlconnection.setDoOutput(true);
			OutputStream outputstream = httpurlconnection.getOutputStream();
			outputstream.write(content);
			outputstream.flush();
			outputstream.close();
			InputStream inputstream = httpurlconnection.getInputStream();
			InputStreamReader inputstreamreader = new InputStreamReader(inputstream, StandardCharsets.US_ASCII);
			BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
			StringBuilder stringbuffer = new StringBuilder();
			String s2;

			while ((s2 = bufferedreader.readLine()) != null) {
				stringbuffer.append(s2);
				stringbuffer.append('\r');
			}

			bufferedreader.close();
			s3 = stringbuffer.toString();
		} finally {
			if (httpurlconnection != null) {
				httpurlconnection.disconnect();
			}
		}

		return s3;
	}

	public static synchronized String getPlayerItemsUrl() {
		if (playerItemsUrl == null) {
			try {
				boolean flag = Config.parseBoolean(System.getProperty("player.models.local"), false);

				if (flag) {
					File file1 = Minecraft.getMinecraft().mcDataDir;
					File file2 = new File(file1, "playermodels");
					playerItemsUrl = file2.toURI().toURL().toExternalForm();
				}
			} catch (Exception exception) {
				Log.error(exception.getClass().getName() + ": " + exception.getMessage());
			}

			if (playerItemsUrl == null) {
				playerItemsUrl = "http://s.optifine.net";
			}
		}

		return playerItemsUrl;
	}
}
