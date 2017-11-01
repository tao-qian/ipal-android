package com.ipalandroid.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * This class is used to download images from the Internet. It first saves the
 * downloaded images in the phone.
 * 
 * @author Tao Qian, DePauw Open Source Development Team
 * 
 */
public class ImageDownloader {

	private static int CONNECTION_TIMEOUT = 30000;
	private static int READ_TIMEOUT = 30000;
	private static String IMAGE_FILE_NAME_PREFIX = "temp_";
	private static String DIRECTORY_ROOT = "IPAL_Android/temp";
	private File directory;

	public ImageDownloader(Context mContext) {
		// Create the directory where the temporary images are saved.
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			directory = new File(
					android.os.Environment.getExternalStorageDirectory(),
					DIRECTORY_ROOT);
		else
			directory = mContext.getCacheDir();
		if (!directory.exists())
			directory.mkdirs();
	}

	/**
	 * This method downloads an image from the Internet using the given URL
	 * 
	 * @param url
	 *            the URL of the image.
	 * @return the bitmap downloaded or null if an error occurred.
	 */
	public Bitmap getImage(String url) {
		Bitmap image = null;
		File f = null;
		try {
			URL imageURL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageURL
					.openConnection();
			conn.setConnectTimeout(CONNECTION_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			f = new File(directory, getImageName(url));
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			image = BitmapFactory.decodeStream(new FileInputStream(f));
		} catch (Exception e) {
			// Log.e("Exception downloading image",e.toString());
			e.printStackTrace();
			image = null;
		}
		if (f != null)
			clear(f);
		return image;
	}

	private static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	/**
	 * This method returns a unique name for a image with given URL
	 * 
	 * @param url
	 *            the URL of the image
	 * @return the name
	 */
	private String getImageName(String url) {
		return IMAGE_FILE_NAME_PREFIX + url.hashCode();
	}

	/**
	 * recursive method used to delete a directory/file
	 * 
	 * @param file
	 *            the directory or file to be deleted
	 */
	public void clear(File file) {
		if (file.isFile())
			file.delete();
		else if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				clear(new File(file, children[i]));
				file.delete();
			}
		}
	}
}
