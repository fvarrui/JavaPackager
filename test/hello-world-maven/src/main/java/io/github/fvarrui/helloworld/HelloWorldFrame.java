package io.github.fvarrui.helloworld;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

@SuppressWarnings("serial")
public class HelloWorldFrame extends JFrame {

	private static String[] args;
	
	public HelloWorldFrame() throws IOException {
		super("Hello World");
		initFrame();
		initContent();
		setVisible(true);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	public void initContent() throws IOException {

		File info = new File("info.txt");

		String content = FileUtils.readFileToString(info, StandardCharsets.UTF_8);

		JTextArea text = new JTextArea();
		text.setFont(new Font("monospaced", Font.PLAIN, 12));
		text.setEditable(false);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(text), BorderLayout.CENTER);

		StringBuffer buffer = new StringBuffer();
		buffer.append("Additional resource: " + info + "\n");
		buffer.append("Content: " + content + "\n\n");

		buffer.append("==============================================\n");
		buffer.append("ARGUMENTS ====================================\n");
		buffer.append("==============================================\n\n");
		buffer.append("args=" + Arrays.asList(args) + "\n");
		buffer.append("\n");

		buffer.append("==============================================\n");
		buffer.append("ENVIRONMENT VARIABLES ========================\n");
		buffer.append("==============================================\n\n");
		List<String> envKeys = System.getenv().keySet().stream().collect(Collectors.toList());
		Collections.sort(envKeys, (a, b) -> a.compareTo(b));
		for (String key : envKeys) {
			buffer.append(key + "=" + System.getenv(key) + "\n");
		}
		buffer.append("\n");

		buffer.append("==============================================\n");
		buffer.append("PROPERTIES ===================================\n");
		buffer.append("==============================================\n\n");
		List<Object> propKeys = System.getProperties().keySet().stream().collect(Collectors.toList());
		Collections.sort(propKeys, (a, b) -> a.toString().compareTo(b.toString()));
		for (Object key : propKeys) {
			buffer.append(key + "=" + System.getProperty("" + key) + "\n");
		}

		text.setText(buffer.toString());

	}

	public void initFrame() throws IOException {
		setSize(640, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(ImageIO.read(getClass().getResourceAsStream("/images/world.png")));
	}

	public static void main(String[] args) {

		HelloWorldFrame.args = args;

		// read double-clicked files on mac os 
//		if (System.getProperty("os.name").contains("OS X")) {
//			
//			java.awt.Desktop.getDesktop().setOpenFileHandler((java.awt.desktop.OpenFilesEvent e) -> {
//				File f = e.getFiles().stream().findFirst().get();
//				HelloWorldFrame.args = new String[] { f.getAbsolutePath() };
//			});
//			
//		}
		
		System.out.println("Starting app ... ");
		System.out.println("PATH=" + System.getenv("PATH"));
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new HelloWorldFrame();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
				
	}

}
