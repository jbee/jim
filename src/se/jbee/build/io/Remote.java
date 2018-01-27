package se.jbee.build.io;

import static java.nio.file.Files.readAllBytes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.security.MessageDigest;
import java.util.Arrays;

import se.jbee.build.Dependency;
import se.jbee.build.Size;

public final class Remote {

	public static void fetch(Dependency dep, boolean replaceExisting) {
		File target = new File(dep.to.file(), dep.source.filename());
		if (target.exists() && !replaceExisting)
			return;
		try {
			File dir = target.getParentFile();
			dir.mkdirs();
			Thread watcher = new Thread(watch(dir));
			watcher.start();
			URL artefact = new URL(dep.source.url);
			System.out.println("Downloading "+artefact+" to "+target);
			System.out.println(Size.from(artefact));
			fetch(artefact, target);
			watcher.interrupt();
			checkSHA1(dep, target);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void checkSHA1(Dependency dep, File data)
			throws MalformedURLException, IOException, FileNotFoundException {
		File target = new File(data.getParentFile(), dep.source.filename()+".sha1");
		if (target.exists())
			return;
		System.out.println("Warning: The .sha1 of the remote dependencies should be checked into source control.");
		URL src = new URL(dep.source.url+".sha1");
		Size sha1 = Size.from(src);
		if (!sha1.isUnknown()) {
			fetch(src, target);
			if (!checkSHA1(data, target)) {
				System.out.println("Checksum does not match!");
			}
		}
	}

	private static void fetch(URL from, File to) throws IOException, FileNotFoundException {
		try (ReadableByteChannel in = Channels.newChannel(from.openStream())) {
			try (FileOutputStream out = new FileOutputStream(to, false)) {
				out.getChannel().transferFrom(in, 0, Long.MAX_VALUE);
			}
		}
	}

	private static boolean checkSHA1(File data, File sha1) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			try(FileInputStream in = new FileInputStream(data)) {
				byte[] buffer = new byte[1024];
				int nread = 0;
				while ((nread = in.read(buffer)) != -1) {
					md.update(buffer, 0, nread);
				}
				return Arrays.equals(encodeHex(md.digest()), readAllBytes(sha1.toPath()));
			}
		} catch (Exception e) {
			return false;
		}
	}

    private static final byte[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	private static byte[] encodeHex(final byte[] data) {
        final byte[] res = new byte[data.length*2];
        for (int i = 0, j = 0; i < data.length; i++) {
            res[j++] = DIGITS[ (0xF0 & data[i]) >>> 4 ];
            res[j++] = DIGITS[ 0x0F & data[i]];
        }
        return res;
    }

	private static Runnable watch(File dir) {
		try {
			WatchService watchService = FileSystems.getDefault().newWatchService();
			Path path = Paths.get(dir.toURI());
			path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
			return () -> {
				try {
					WatchKey key;
					while ((key = watchService.take()) != null) {
						for (WatchEvent<?> event : key.pollEvents()) {
							System.out.println("File affected: " + event.context() + " "
									+ new File(dir, event.context().toString()).length());

						}
						key.reset();
					}
				} catch (InterruptedException e) {
					try {
						watchService.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return; // end the watch
				}
			};
		} catch (Exception e) {
			return null;
		}
	}
}
