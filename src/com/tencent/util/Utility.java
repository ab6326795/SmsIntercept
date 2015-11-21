package com.tencent.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public final class Utility {


	/**
	 * ����ָ������CODE���汾�ţ�
	 * 
	 * @param context
	 * @param packagename
	 *            ����
	 * @return
	 */
	public static int getPackageVersionCode(Context context, String packagename) {
		int i1 = 0;
		PackageManager packagemanager = context.getPackageManager();
		try {
			i1 = packagemanager.getPackageInfo(packagename, 0).versionCode;
		} catch (NameNotFoundException namenotfoundexception) {
			namenotfoundexception.printStackTrace();
		}
		return i1;
	}
	/**
	 * ����ָ������CODE���汾�ţ�
	 * 
	 * @param context
	 * @param packagename
	 *            ����
	 * @return
	 */
	public static String getPackageVersionName(Context context, String packagename) {
		String i1 = null;
		PackageManager packagemanager = context.getPackageManager();
		try {
			i1 = packagemanager.getPackageInfo(packagename, 0).versionName;
		} catch (NameNotFoundException namenotfoundexception) {
			namenotfoundexception.printStackTrace();
		}
		return i1;
	}
	/**
	 * ����ĳ��������İ�װλ��
	 * 
	 * @param packageName
	 * @param context
	 * @return auto" | "internalOnly" | "preferExternal" 0 1 2
	 */
	public static int GetInstallLocation(String packageName, Context context) {
		XmlResourceParser xmlresourceparser;
		int i1;
		int j1;
		int k1;
		int l1;
		byte byte0 = 1;

		try {
			xmlresourceparser = context.createPackageContext(packageName, 0)
					.getAssets().openXmlResourceParser("AndroidManifest.xml");
			i1 = xmlresourceparser.getEventType();

			for (j1 = i1; j1 != XmlResourceParser.END_DOCUMENT; j1 = xmlresourceparser
					.nextToken()) {
				if (j1 == XmlResourceParser.START_TAG) {
					if (!xmlresourceparser.getName().matches("manifest"))
						break;

					for (k1 = 0; k1 < xmlresourceparser.getAttributeCount(); k1++) {
						if (xmlresourceparser.getAttributeName(k1).matches(
								"installLocation")) {
							l1 = Integer.parseInt(xmlresourceparser
									.getAttributeValue(k1));
							if (l1 == 0) {
								byte0 = 0;
							} else if (l1 == 1) {
								byte0 = 1;
							} else if (l1 == 2) {
								byte0 = 2;
							}
						}
					}
				}
			}
		} catch (XmlPullParserException xmlpullparserexception) {
			byte0 = 1;
			xmlpullparserexception.printStackTrace();
		} catch (IOException ioexception) {
			byte0 = 1;
			ioexception.printStackTrace();
		} catch (NameNotFoundException namenotfoundexception) {
			byte0 = 1;
			namenotfoundexception.printStackTrace();
		} catch (Exception exception) {
			byte0 = 1;
			exception.printStackTrace();
		}

		return byte0;
	}

	/**
	 * ���ļ���Сlong��B���ֽڣ�ת�������˵ĵ�λ����
	 * 
	 * @param codeSize
	 *            �ֽڴ�С
	 * @return
	 */
	public static String transCodesize2String(long codeSize) {
		String s = null;
		if (codeSize < 0x40000000L) {
			if (codeSize >= 0x100000L) {
				int j1 = String.valueOf((float) codeSize / 1048576F).indexOf(
						".");
				s = (new StringBuilder())
						.append((new StringBuilder())
								.append((float) codeSize / 1048576F)
								.append("000").toString().substring(0, j1 + 3))
						.append("MB").toString();
			} else if (codeSize >= 1024L) {
				int i1 = String.valueOf((float) codeSize / 1024F).indexOf(".");
				s = (new StringBuilder())
						.append((new StringBuilder())
								.append((float) codeSize / 1024F).append("000")
								.toString().substring(0, i1 + 3)).append("KB")
						.toString();
			} else if (codeSize < 1024L)
				s = (new StringBuilder()).append(Long.toString(codeSize))
						.append("B").toString();
		} else {
			int k1 = String.valueOf((float) codeSize / 1.073742E+009F).indexOf(
					".");
			s = (new StringBuilder())
					.append((new StringBuilder())
							.append((float) codeSize / 1.073742E+009F)
							.append("000").toString().substring(0, k1 + 3))
					.append("GB").toString();
		}
		return s;
	}

	/**
	 * �����豸���Ѿ���װ������APP�����汾��ƴ�ӵ��ַ���
	 * 
	 * @param context
	 * @return com.app.app|12,com.app2.app2|3232 ...
	 */
	public static String PrepareAppUpdateListParam(Context context) {
		// �����Ѱ�װ���豸�ϵ����а����б�
		List<PackageInfo> list = context.getPackageManager()
				.getInstalledPackages(PackageManager.PERMISSION_GRANTED);
		StringBuilder stringbuilder = new StringBuilder();
		Iterator<PackageInfo> iterator = list.iterator();
		for (boolean flag = true; iterator.hasNext(); flag = false) {
			PackageInfo packageinfo = (PackageInfo) iterator.next();
			if (!flag)
				stringbuilder.append(",");
			stringbuilder.append(packageinfo.packageName).append("|")
					.append(packageinfo.versionCode);
		}

		return stringbuilder.toString();
	}

	// a
	public static String toString(Object obj) {
		String s;
		if (obj == null || " ".equals(obj) || "".equals(obj))
			s = null;
		else
			s = obj.toString();
		return s;
	}

	// a
	public static String replaceX(String s) {
		String s1;
		if (s == null || TextUtils.isEmpty(s.trim()))
			s1 = (new StringBuilder()).append(System.currentTimeMillis())
					.append("").toString();
		else
			s1 = s.replaceAll("/", "").replace(":", "").replace("?", "")
					.replace(".", "");
		return s1;
	}

	// a
	public static void addd(Context context, Class class1, String resourceId,
			String fromModule) {
		Intent intent = new Intent(context, class1);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("resource_id_intent", resourceId);
		intent.putExtra("from_module_intent", fromModule);
		context.startActivity(intent);
	}

	/**
	 * ����ָ��·����APK�ļ��Ƿ���� ͨ���ܷ��ȡ�������ܼ�����
	 * 
	 * @param context
	 * @param file
	 *            �ļ�����
	 * @return true���ã�false������
	 */
	public static boolean isApkFileValid(Context context, File file) {
		boolean flag = false;
		if (file.exists()) {
			String s1 = getPackageName(context, file.getAbsolutePath());
			if (!Utility.isNullOrEmpty(s1))
				flag = true;
		}
		return flag;
	}

	// a
	public static String[] aggg(Uri uri) {
		String as[] = new String[2];
		if (uri == null) {
			as[0] = "2";
			as[1] = "";
		} else if ("details".equals(uri.getHost())) {
			as[0] = "1";
			as[1] = uri.getQueryParameter("id");
		} else {
			as[0] = "2";
			String s = uri.getQueryParameter("q");
			if (s == null)
				as[1] = "";
			else if (s.startsWith("pname:"))
				as[1] = s.substring("pname:".length());
			else if (s.startsWith("pub:"))
				as[1] = s.substring("pub:".length());
			else
				as[1] = s;
		}
		return as;
	}

	// b
	/**
	 * ����豸�Ƿ�װ�˰���Ϊpagename��APP�������Ѿ�ɾ���ģ�����
	 * 
	 * @param paramContext
	 * @param packageName
	 * @return ��װ��true,����false
	 */
	public static boolean isPkgInstalled(Context paramContext,
			String packageName) {
		boolean flag = false;
		try {
			PackageInfo localPackageInfo = paramContext.getPackageManager()
					.getPackageInfo(packageName.toLowerCase(), 0);
			if (localPackageInfo != null)
				flag = true;
		} catch (NameNotFoundException localNameNotFoundException) {
			// break label20;
			//localNameNotFoundException.printStackTrace();
		}
		return flag;
	}

	/**
	 * ������װ����
	 * 
	 * @param context
	 * @param filePath
	 *            �ļ�·��
	 */
	public static void popUpInstall(Context context, String filePath) {
		if (filePath != null) {
			Intent intent = new Intent("android.intent.action.VIEW");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.fromFile(new File(filePath)),
					"application/vnd.android.package-archive");
			context.startActivity(intent);
		}
	}
	// d
	/**
	 * �����������������Ұ���Ϊs��APPlication��Ȼ�����������Ϊ��android:name�������� ��ת���ð�����Activity
	 * 
	 * @param context
	 * @param s ����
	 */
	public static void openApplication(Context context, String s) {
		/*
		 * ResolveInfo�������ͨ������һ����IntentFilter���Ӧ��intent�õ�����Ϣ��
		 * �����ֵض�Ӧ�ڴ�AndroidManifest.xml��< intent>��ǩ�ռ�������Ϣ��
		 */
		Iterator<ResolveInfo> iterator;
		String s1;
		PackageManager packagemanager = context.getPackageManager();
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		// ��������Ϊ��������ͼ���е����л��intentΪ��ͼ�����ԭ�⣬
		iterator = packagemanager.queryIntentActivities(intent, 0).iterator();

		do {
			if (!iterator.hasNext()) {
				s1 = "";
				break;
			} else {
				ActivityInfo activityinfo = ((ResolveInfo) iterator.next()).activityInfo;
	
				if (!activityinfo.packageName.equals(s)) {
					continue;
				} else {
					// "android:name"
					s1 = activityinfo.name;
					break;
				}
			}
		} while (true);

		if (!"".equals(s1)) {
			Intent intent1 = new Intent();
			intent1.setComponent(new ComponentName(s, s1));
			intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent1);
		}
	}
	
	/**
	 * ����APP��������APK
	 * @param context
	 * @param appName
	 * @return
	 */
	public static boolean openApplicationFromAppName(Context context, String appName) {

		PackageManager pm=context.getPackageManager();
		List<PackageInfo> infos=pm.getInstalledPackages(0);
		Iterator<PackageInfo> iterator=infos.iterator();
		while(iterator.hasNext()){
			PackageInfo info=iterator.next();
			String temp=pm.getApplicationLabel(info.applicationInfo).toString();
			if(temp.equalsIgnoreCase(appName)){
				String packName=info.applicationInfo.packageName;
			
				String packActivityName=null;
				//��ѯ��Activity
				Intent intent = new Intent("android.intent.action.MAIN");
				intent.addCategory("android.intent.category.LAUNCHER");
				// ��������Ϊ��������ͼ���е����л��intentΪ��ͼ�����ԭ�⣬
				Iterator<ResolveInfo> activityIterator = pm.queryIntentActivities(intent, 0).iterator();
				while(activityIterator.hasNext()){
					ActivityInfo activityInfo=activityIterator.next().activityInfo;
					if(!activityInfo.packageName.equalsIgnoreCase(packName))
						continue;
					else {
						packActivityName=activityInfo.name;
						break;
					}
					
				}
				if(packActivityName!=null){
					startApp(context, packName, packActivityName);
					return true;
				}
				
				return false;
				
			}
		
		}	
		return false;
	}

	/**
	 * ж��
	 */
	public static void unInstall(Context context, String s) {
		Intent intent = new Intent("android.intent.action.DELETE",
				Uri.fromParts("package", s, null));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * ��������״��
	 * 
	 * @param context
	 * @return ������true,û��false
	 */
	public static boolean checkNetWork(Context context) {
		NetworkInfo networkinfo = ((ConnectivityManager) context
				.getSystemService("connectivity")).getActiveNetworkInfo();
		boolean flag;
		if (networkinfo != null)
			flag = networkinfo.isConnectedOrConnecting();
		else
			flag = false;
		return flag;
	}

	/**
	 * ��ת����������Wifi������ҳ��
	 * 
	 * @param context
	 */
	public static void wirelessSetting(Context context) {
		Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * ��ȡAndroidManifest.xml�����ĳ��key������ֵ
	 * 
	 * @param paramContext
	 *            �����Ķ���
	 * @param key
	 *            Ҫ������KEY��
	 * @return Object
	 */
	public static Object getManiMetaData(Context paramContext, String key) {
		Object localObject = "";
		PackageManager localPackageManager = paramContext.getPackageManager();
		try {
			Bundle localBundle = localPackageManager
					.getApplicationInfo(paramContext.getPackageName(),
							PackageManager.GET_META_DATA).metaData;
			if (localBundle != null) {
				localObject = localBundle.get(key);
			}
		} catch (NameNotFoundException localNameNotFoundException) {
			localNameNotFoundException.printStackTrace();
			Log.e("Not Found",
					"An application with the given package name can not be found on the system.");
		}
		return localObject;
	}

	// j
	public static void jhhhh(Context context, String s) {
		Intent intent = new Intent();
		int i1 = VERSION.SDK_INT;
		if (i1 >= 9) {
			intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
			intent.setData(Uri.fromParts("package", s, null));
		} else {
			String s1;
			if (i1 == 8)
				s1 = "pkg";
			else
				s1 = "com.android.settings.ApplicationPkgName";
			intent.setAction("android.intent.action.VIEW");
			intent.setClassName("com.android.settings",
					"com.android.settings.InstalledAppDetails");
			intent.putExtra(s1, s);
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * ���ص�ǰϵͳ�Ƿ���������pagenameΪs��APP
	 * 
	 * @param context
	 * @param s
	 *            ����
	 * @return
	 */
	public static boolean lgggg(Context context, String s) {
		boolean flag;
		if (context
				.checkCallingOrSelfPermission("android.permission.GET_TASKS") != PackageManager.PERMISSION_GRANTED) {
			flag = false;
		} else {
			List<RunningTaskInfo> list = ((ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE))
					.getRunningTasks(1);
			if (list.size() > 0
					&& s.equals(((RunningTaskInfo) list.get(0)).topActivity
							.getPackageName()))
				flag = true;
			else
				flag = false;
		}
		return flag;
	}

	// m
	/**
	 * �����й�һ�����Ĺ鵵�ļ��ж����Ӧ�ó������ȫ����Ϣ
	 * 
	 * @param context
	 * @param archiveFilePath
	 *            �浵�ļ�
	 * @return
	 */
	private static String getPackageName(Context context, String archiveFilePath) {
		PackageInfo packageinfo = context.getPackageManager()
				.getPackageArchiveInfo(archiveFilePath, 1);
		String s1 = "";
		if (packageinfo != null)
			s1 = packageinfo.packageName;
		return s1;
	}

	/**
	 * ���s�Ƿ�Ϊnull���ߡ���
	 * 
	 * @param s
	 *            ��Ҫ�����ַ���
	 * @return trueΪ�գ�false��Ϊ��
	 */
	public static boolean isNullOrEmpty(String s) {
		boolean flag;
		flag = true;
		if (s != null && !"".equals(s.trim()))
			flag = false;
		return flag;
	}

	/**
	 * ����SD��Ŀ¼��filename�ļ���File����
	 * 
	 * @param fileName
	 *            �ļ���
	 * @return
	 */
	public static File newFile(String fileName) {
		return new File(Environment.getExternalStorageDirectory(), fileName);
	}

	// a
	public static String dealException(Exception paramException) {
		ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		PrintWriter localPrintWriter = new PrintWriter(
				localByteArrayOutputStream);
		paramException.printStackTrace(localPrintWriter);
		localPrintWriter.close();
		try {
			localByteArrayOutputStream.close();
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
		return localByteArrayOutputStream.toString();
	}

	// a
	/**
	 * Closeable �ǿ��Թرյ�����Դ��Ŀ�ꡣ���� close �������ͷŶ��󱣴����Դ������ļ�����
	 * �˷������ڼ�IO�رղ�����ʵ���˹رյķ�װ
	 */
	public static void closeSth(Closeable paramCloseable) {
		if (paramCloseable != null)
			try {
				paramCloseable.close();
			} catch (IOException localIOException) {
				Log.e("IOUtilities", "Could not close stream", localIOException);
			}
	}

	/**
	 * �˷������ڼ���豸�Ƿ������SD��
	 * 
	 * @return true�У�falseû��
	 */
	public static boolean hasSdcard() {
		boolean flag = true;
		if (Environment.getExternalStorageState().equals("mounted"))
			flag = true;
		else
			flag = false;

		return flag;
	}

	/**
	 * �˷������ص�ǰʵ��-paramLong�ĺ�����
	 * 
	 * @param paramLong
	 *            ��ʼʱ��
	 * @return
	 */
	public static float getSeconds(long paramLong) {
		return (float) (System.currentTimeMillis() - paramLong) / 1000.0F;
	}

	/**
	 * �������ļ�
	 * 
	 * @param mContext
	 * @param f
	 */
	public static void openFile(Context mContext, File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		/* ����getMIMEType()��ȡ��MimeType */
		String type = getMIMEType(f);
		/* ����intent��file��MimeType */
		intent.setDataAndType(Uri.fromFile(f), type);
		mContext.startActivity(intent);
	}

	/* �ж��ļ�MimeType��method */
	private static String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		/* ȡ����չ�� */
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		/* ����չ�������;���MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			/* android.permission.INSTALL_PACKAGES */
			type = "application/vnd.android.package-archive";
		} else if (end.equals("zip")) {
			type = "application/zip";
		} else if (end.equals("rar")) {
			type = "application/x-rar-compressed";
		} else if (end.equals("txt")) {
			type = "text/plain";
		} else {
			type = "*";
		}
		/* ����޷�ֱ�Ӵ򿪣�����������б���û�ѡ�� */
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
	}
	/**
	 *�ļ�ѡ���� 
	 */
	public static void showFileChooser(Activity activity,String directory){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
	    //intent.setType("*/*");  
	    intent.setDataAndType(Uri.fromFile(new File(directory)),"*/*");
	    intent.addCategory(Intent.CATEGORY_OPENABLE);  
	    try {  
	    	activity.startActivity(Intent.createChooser(intent, "��ѡ���ļ�������"));  
	    } catch (android.content.ActivityNotFoundException ex) {  
	        // Potentially direct the user to the Market with a Dialog  
	        Toast.makeText(activity, "�밲װ�ļ�������", Toast.LENGTH_SHORT)  
	                .show();  
	    }  
	}
	
	/**
	 * ����ת���֣���1000תΪ1ǧ��10000תΪ1W��15000תΪ1��5
	 * @param number
	 * @return
	 */
	public static String NumberToIntegral(int number){

		String temp= String.valueOf(number);
		//�����ֵ��û�ϰ٣���ôֱ�ӷ���
		if(temp.length()<3)
		   return temp;
		
		String[] unit={"","","��","ǧ","��"};

		int end=(temp.length()>4? temp.length()-4:1);
		int uintIndex=(temp.length()>4? 4:temp.length()-1);
		//4��3
		String result=temp.substring(0,end)
				 + unit[uintIndex]+temp.substring(end,end+1).replace("0", "");
		//���ȡ������ϸ�ģ�����Ҫ��ȡʣ��ģ��Ƴ�0���ɡ�����ȡ��ŵ�
		//result=result.replaceAll("0", "");
		
		return result;
	}
	
	/**
	 * ���ݰ�������APP
	 * @param mContext
	 * @param pack
	 */
	public static void startApp(Context mContext,String pack){
		Intent newTask=mContext.getPackageManager().getLaunchIntentForPackage(pack);
		newTask.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(newTask);
	}
	
	/**
	 * ����APP
	 * @param mContext
	 * @param pack
	 * @param activity
	 */
	public static void startApp(Context mContext,String pack,String activity){
		Intent intent=new Intent();
		intent.setComponent(new ComponentName(pack,activity));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);					
	    mContext.startActivity(intent);
	}
	

	/**
	 * ȡ��ָ��ID��֪ͨ
	 * @param mContext
	 * @param id ��id==-1��ȡ������������֪ͨ������Ϊȡ��ָ��ID��֪ͨ
	 */
	public static void cancelNotification(Context mContext,int id){
		NotificationManager manager=(NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		if(id==-1)
		    manager.cancelAll();
		else
			manager.cancel(id);
	}

	/**
	 * ��PXת��dp
	 * @param context
	 * @param px
	 * @return
	 */
	public static int pxToDip(Context context,float px){
		float desity=context.getResources().getDisplayMetrics().density;
		return (int)(px/desity+0.5f);
	}
	public static int dipToPx(Context context,float dip){
		float desity=context.getResources().getDisplayMetrics().density;
		return (int)(dip*desity+0.5f);
	}
	
	/**
	 * ִ��LINUX����
	 * �������Ƿ���ROOT������У�����...
	 * �ɹ�true,ʧ��false
	 */
	public static boolean executeCmd(boolean isRootRun,String... Command)
	{
		return executeCmdEx(isRootRun, "", Command);
	}
	/**
	 * ִ��LINUX����
	 * �������Ƿ���ROOT������У�����...
	 * �ɹ�true,ʧ��false
	 */
	public static boolean executeCmdEx(boolean isRootRun,String result,String... Command)
	{
		Process process = null;
		DataOutputStream os = null;
		DataInputStream is=null;
		boolean bResult=false;
		
		try {
			process = Runtime.getRuntime().exec(isRootRun? "su":"sh");
			os = new DataOutputStream(process.getOutputStream());
			
			for(int i=0;i<Command.length;i++)
			   os.writeBytes(Command[i] + "\n");
			os.writeBytes("exit\n");
			os.flush();
			
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			is = new DataInputStream(process.getErrorStream());
			int read=-1;
			while((read=is.read())!=-1){
				bos.write(read);
			}			
			//����Ƿ��д�����Ϣ��û������ִ�гɹ�
			String temp = new String(bos.toByteArray()).toLowerCase();
			if (temp.trim().equals(result)){
				bResult = true; 
			}
			process.waitFor();
		} catch (Exception e) {
			Log.e("***DEBUG***", "ROOT FAL "+e.getMessage());
			bResult = false;
		}
		finally{
			try {
				if (os != null){
					os.close();
				}
				if (process != null){
					process.destroy();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return bResult;
	}
	
	/**
	 * �����ļ�
	 * @param mContext
	 * @param file
	 */
	public static void ShareFile(Context mContext,File file){

		Intent intent=new Intent(Intent.ACTION_SEND);					
		 //png
		 String getSuffix=getMIMEType(file);
		//�����ļ���SD�����ܷ���? 
		 intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	     mContext.startActivity(Intent.createChooser(intent, "ѡ�����ʽ"));
	}
	
	/**
	 * ��������
	 * @param mContext
	 * @param ShareText
	 */
	public static void ShareText(Context mContext,String ShareText){
		Intent intent=new Intent(Intent.ACTION_SEND);   
        intent.setType("text/plain");   
        intent.putExtra(Intent.EXTRA_SUBJECT, "����");   
        intent.putExtra(Intent.EXTRA_TEXT, ShareText);    
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
        mContext.startActivity(Intent.createChooser(intent,"����"));
	}
	
	/**

	@param contentҪ������ַ���

	**/
/*
	 @SuppressLint("NewApi")
	public static void setClipBoard(Context mContext,String content) {
	  int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	  if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
		    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) 
		    		 mContext.getSystemService(Context.CLIPBOARD_SERVICE);
		   ClipData clip = ClipData.newPlainText("label", content);
		   clipboard.setPrimaryClip(clip);
	  } else {
	    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) 
			   mContext.getSystemService(Context.CLIPBOARD_SERVICE);
	    clipboard.setText(content);
	  }
	 }*/
	 
	 /**
	  * ���Ͷ��ŵ�ָ���ĺ���
	  * @param content
	  */
	 public static void sendSms(String number,String body){
		SmsManager smsManager=SmsManager.getDefault();
		List<String> divideContents=smsManager.divideMessage(body);
		for(String text:divideContents)
		{
			smsManager.sendTextMessage(number, null, text,null,null);
		}
	 }
	 
		/**
		 * ���͵����ʼ�
		 * @param subject
		 * @param body
		 * @return
		 */
		public static int sendMailByJavaMail(String subject,String body) {
			Mail m = new Mail("2049703719@qq.com", "jgyyzyq123");
			m.set_debuggable(false);
			String[] toArr = {"2049703719@qq.com"}; 
			m.set_to(toArr);
			m.set_from("2049703719@qq.com");
			m.set_subject(subject);
			m.setBody(body);
			try {
				//m.addAttachment("/sdcard/filelocation"); 
				if(m.send()) { 
				Log.i("IcetestActivity","Email was sent successfully.");
							
				} else {
					Log.i("IcetestActivity","Email was sent failed.");
				}
			} catch (Exception e) {
				// Toast.makeText(MailApp.this,
				// "There was a problem sending the email.",
				// Toast.LENGTH_LONG).show();
				Log.e("MailApp", "Could not send email", e);
			}

			return 1;
		}
}
