/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class SerialPort {

	private static final String TAG = "SerialPort";

	/*
	 * Do not remove or rename the field mFd: it is used by native method
	 * close();
	 */
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public SerialPort(File device, int baudrate, int bits, char event,
			int stop, int flags) throws SecurityException, IOException {
		//System.loadLibrary("serialport");
		if(android.os.Build.VERSION.SDK_INT >= 21){
			System.loadLibrary("serialport-arm64");
			Log.v(TAG,"SerialPort  libserialport-arm64");
		}
		else
		System.loadLibrary("serialport");

		if (!device.canRead() || !device.canWrite()) {
			try {
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 777 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
			Log.v(TAG,"su.waitFor(): "+su.waitFor()+" device.canRead(): "+device.canRead()+" device.canWrite(): "+device.canWrite());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}
		mFd = open(device.getAbsolutePath(), baudrate, bits, event, stop, flags);
		Log.i("info", "open device!!");
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);

	}

	public SerialPort(File device, int baudrate, int flags, boolean isInfrared)
			throws SecurityException, IOException {
//		System.loadLibrary("serial_port");
		//System.loadLibrary("serialport");
		if(android.os.Build.VERSION.SDK_INT >= 21){
			System.loadLibrary("serialport-arm64");
			Log.v(TAG,"SerialPort  libserialport-arm64");
		}
		else
		System.loadLibrary("serialport");

		/* Check access permission */
		if (!device.canRead() || !device.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				Process su;
				su = Runtime.getRuntime().exec("/system/bin/su");
				String cmd = "chmod 777 " + device.getAbsolutePath() + "\n"
						+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead()
						|| !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}

		mFd = open(device.getAbsolutePath(), baudrate, flags);
		if (mFd == null) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	// JNI
	private native static FileDescriptor open(String path, int baudrate,
			int flags);

	private native static FileDescriptor open(String path, int baudrate,
			int bits, char event, int stop, int flags);

	//private native static FileDescriptor openwithflag(String path,
	//		int baudrate, int bits, char event, int stop, int flags);

	public native void close();
	// static {
	//
	// System.loadLibrary("serialport");
	// }

}
