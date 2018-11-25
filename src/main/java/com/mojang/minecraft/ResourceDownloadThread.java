package com.mojang.minecraft;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceDownloadThread extends Thread
{
	public ResourceDownloadThread(File minecraftFolder, Minecraft minecraft)
	{
		this.minecraft = minecraft;

		this.setName("Resource downloadUrl thread");
		this.setDaemon(true);

		dir = new File(minecraftFolder, "resources/");

		if(!dir.exists() && !dir.mkdirs())
		{
			throw new RuntimeException("The working directory could not be created: " + dir);
		}
	}

	@Override
	public void run()
	{
		BufferedReader reader = null;

		List<String> list = new ArrayList<String>();
		File baseSoundsSourceDir = new File("sounds");
		try {
			URL base = null;
			URL url = null;
			URLConnection con = null;

			try {
				base = new URL("http://dl.dropbox.com/u/40737374/minecraft_resources/");
				url = new URL(base, "resources/");

				con = url.openConnection();

				con.setConnectTimeout(20000);

				if (con instanceof HttpURLConnection) {
					int code = ((HttpURLConnection) con).getResponseCode();
					String status = ((HttpURLConnection) con).getResponseMessage();
					if (code != 200) {
						((HttpURLConnection) con).disconnect();
						con = null;
						System.err.println("WARN:  A request to URL \"" + url.toString() + "\" returned " + code + " http response code with status \"" + status + "\".");
					}
				}
			} catch (UnknownHostException unknownHost) {
				unknownHost.printStackTrace();
				con = null;
			}

			if (con != null) {
				reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

				String line = null;

				while ((line = reader.readLine()) != null) {
					list.add(line);
				}

				reader.close();
			} else {
				list = listOfSoundsFromLocalFile(new File(baseSoundsSourceDir, "list.csv"));
			}

			int numErrors = 0;
			for(String s : list)
			{
				if (s == null || s.trim().startsWith("//") || s.trim().startsWith("#")) {
					continue;
				}
				String split[] = s.split(",");
				if (split == null || split.length < 2) {
					numErrors++;
					if (numErrors < 3) {
						System.out.println("ERROR: Line Content: \"" + s + "\". Cannot split line into two elements separated by comma (,). Expected format is \"<filename>,<size>\".");
					}
					continue;
				}
				String path = split[0];
				int size = -1;
				try {
					size = Integer.parseInt(split[1]);
				} catch (NumberFormatException nfe) {
					// Ignored
				}

				File file = new File(dir, path);

				File musicFolder = new File(dir, "music");

				if(!file.exists() || file.length() != size)
				{
					try {
						file.getParentFile().mkdirs();
					} catch(SecurityException e) {
						e.printStackTrace();
					}



					if(file.getPath().contains("music"))
					{
						if(file.getName().equals("minecraft.ogg") && !new File(musicFolder, "calm1.ogg").exists())
						{
							download(path, base, baseSoundsSourceDir, file, size);
						} else if(file.getName().equals("clark.ogg") && !new File(musicFolder, "calm2.ogg").exists()) {
							download(path, base, baseSoundsSourceDir, file, size);
						} else if(file.getName().equals("sweden.ogg") && !new File(musicFolder, "calm3.ogg").exists()) {
							download(path, base, baseSoundsSourceDir, file, size);
						}
					} else {
						download(path, base, baseSoundsSourceDir, file, size);
					}
				}

				File minecraftOGG = new File(musicFolder, "minecraft.ogg");
				File clarkOGG = new File(musicFolder, "clark.ogg");
				File swedenOGG = new File(musicFolder, "sweden.ogg");

				minecraftOGG.renameTo(new File(musicFolder, "calm1.ogg"));
				clarkOGG.renameTo(new File(musicFolder, "calm2.ogg"));
				swedenOGG.renameTo(new File(musicFolder, "calm3.ogg"));
			}

			File soundsFolder = dir;
			File stepsFolder = new File(soundsFolder, "step");

			for(int i = 1; i <= 4; i++)
			{
				minecraft.sound.registerSound(new File(stepsFolder, "grass" + i + ".ogg"), "step/grass" + i + ".ogg");
				minecraft.sound.registerSound(new File(stepsFolder, "gravel" + i + ".ogg"), "step/gravel" + i + ".ogg");
				minecraft.sound.registerSound(new File(stepsFolder, "stone" + i + ".ogg"), "step/stone" + i + ".ogg");
				minecraft.sound.registerSound(new File(stepsFolder, "wood" + i + ".ogg"), "step/wood" + i + ".ogg");
			}

			File musicFolder = new File(dir, "music");

			for(int i = 1; i <= 3; i++)
			{
				minecraft.sound.registerMusic("calm" + i + ".ogg", new File(musicFolder, "calm" + i + ".ogg"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		this.finished = true;
	}

	private List<String> listOfSoundsFromLocalFile(File soundsCsv) {
		List<String> list = new ArrayList<String>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(soundsCsv)));
			String line = null;
			while ((line = in.readLine()) != null){
				list.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}


	private File dir;
	private Minecraft minecraft;
	boolean running = false;

	private boolean finished = false;
	private int progress = 0;

	private void download(String path, URL url, File dir, File dest, int size) {
		boolean success = true;
		try {
		 	success = downloadUrl(url, path, dest, size);
		} catch (Throwable t) {
			success = false;
		}
		if (!success) {
			downloadLocalFile(dir, path, dest, size);
		}
	}

	private boolean downloadLocalFile(File base, String path, File file, int size) {
		boolean success = true;
		System.out.print("INFO:  Downloading: " + file.getName() + " from \"" + base.getAbsolutePath() + "\" ...");
		DataInputStream in = null;
		DataOutputStream out = null;
		try {
			byte[] data = new byte[4096];

			in = new DataInputStream(new FileInputStream(new File(base, path)));
			FileOutputStream fos = new FileOutputStream(file);
			out = new DataOutputStream(fos);
			int done = 0;
			do {
				int length = in.read(data);
				if(length < 0) {
					in.close();
					out.close();
					break;
				}
				out.write(data, 0, length);
				done += length;
				progress = (int)(((double)done / (double)size) * 100);
			} while(!running);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			success = false;
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		} finally {
			try {
				if(in != null) {
					in.close();
				}
				if(out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				success = false;
			}
		}
		progress = 0;
		if (success) {
			System.out.println(" Downloaded");
		} else {
			System.out.println();
		}
		return success;
	}

	private boolean downloadUrl(URL base, String path, File file, int size) throws MalformedURLException {
		boolean success = true;
		URL url = new URL(base, path.replaceAll(" ", "%20"));
		System.out.print("INFO:  Downloading: " + file.getName() + " from \"" + url.toString() + "\" ...");

		DataInputStream in = null;
		DataOutputStream out = null;

		try {
			byte[] data = new byte[4096];

			URLConnection connection = url.openConnection();
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection)connection;
				int code = httpConnection.getResponseCode();
				String message = httpConnection.getResponseMessage();
				if (code != 200) {
					System.out.println("  " + code + " " + message);
					httpConnection.disconnect();
					return false;
				}
			}
			in = new DataInputStream(connection.getInputStream());
			FileOutputStream fos = new FileOutputStream(file);
			out = new DataOutputStream(fos);
			int done = 0;
			do {
				int length = in.read(data);

				if(length < 0) {
					in.close();
					out.close();
					break;
				}
				out.write(data, 0, length);
				done += length;
				progress = (int)(((double)done / (double)size) * 100);
			} while(!running);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			success = false;
		} catch (IOException e) {
			success = false;
		} finally {
			try {
				if(in != null) {
					in.close();
				}
				if(out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
				success = false;
			}
		}

		progress = 0;
		if (success) {
			System.out.println(" Downloaded");
		} else {
			System.out.println();
		}
		return success;
	}

	public boolean isFinished()
	{
		return finished;
	}

	public int getProgress()
	{
		return progress;
	}
}
