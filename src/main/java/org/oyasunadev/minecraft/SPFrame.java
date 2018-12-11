package org.oyasunadev.minecraft;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.MinecraftApplet$1;
import com.mojang.minecraft.level.LevelIO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public final class SPFrame extends JFrame implements WindowListener
{

	private Minecraft minecraft = null;

	public SPFrame()
	{
		setTitle("MinecraftMania - Single Player");
		setSize(854, 480);
		setResizable(false);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Commmented because we implement window listener now.
		addWindowListener(this);
		setLayout(new BorderLayout());

		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;

		if(getWidth() == width && getHeight() == height)
		{
			setUndecorated(true);
		}
	}

	public void startMinecraft()
	{
		MCApplet applet = new MCApplet();
		MinecraftApplet$1 canvas = new MinecraftApplet$1(applet);

		this.minecraft = new Minecraft(canvas, applet, getWidth(), getHeight(), false);

		canvas.setSize(getWidth(), getHeight());

		add(canvas, "Center");

		pack();

		new Thread(this.minecraft).start();
	}

	public void finish()
	{
		setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - getWidth()) / 2,
				(Toolkit.getDefaultToolkit().getScreenSize().height - getHeight()) / 2);
	}

	/**
	 * Invoked the first time a window is made visible.
	 *
	 * @param e the event to be processed
	 */
	@Override
	public void windowOpened(WindowEvent e) {

	}

	/**
	 * Invoked when the user attempts to close the window
	 * from the window's system menu.
	 *
	 * @param e the event to be processed
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		int status = 0;
		if (this.minecraft != null && this.minecraft.level != null) {
			File levelDat = new File("level.dat");
			try {
				LevelIO.save(this.minecraft.level, (OutputStream) (new FileOutputStream(levelDat)));
			} catch (FileNotFoundException fnfe) {
				System.err.println("ERROR:  Cannot save current level into file \"" + levelDat.getAbsolutePath() + "\" ("
						+ fnfe.getClass().getName() + " : " + fnfe.getLocalizedMessage() + ").");
				fnfe.printStackTrace();
				status = 1;
			}
		}
		System.exit(status);
	}

	/**
	 * Invoked when a window has been closed as the result
	 * of calling dispose on the window.
	 *
	 * @param e the event to be processed
	 */
	@Override
	public void windowClosed(WindowEvent e) {

	}

	/**
	 * Invoked when a window is changed from a normal to a
	 * minimized state. For many platforms, a minimized window
	 * is displayed as the icon specified in the window's
	 * iconImage property.
	 *
	 * @param e the event to be processed
	 * @see Frame#setIconImage
	 */
	@Override
	public void windowIconified(WindowEvent e) {

	}

	/**
	 * Invoked when a window is changed from a minimized
	 * to a normal state.
	 *
	 * @param e the event to be processed
	 */
	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	/**
	 * Invoked when the Window is set to be the active Window. Only a Frame or
	 * a Dialog can be the active Window. The native windowing system may
	 * denote the active Window or its children with special decorations, such
	 * as a highlighted title bar. The active Window is always either the
	 * focused Window, or the first Frame or Dialog that is an owner of the
	 * focused Window.
	 *
	 * @param e the event to be processed
	 */
	@Override
	public void windowActivated(WindowEvent e) {

	}

	/**
	 * Invoked when a Window is no longer the active Window. Only a Frame or a
	 * Dialog can be the active Window. The native windowing system may denote
	 * the active Window or its children with special decorations, such as a
	 * highlighted title bar. The active Window is always either the focused
	 * Window, or the first Frame or Dialog that is an owner of the focused
	 * Window.
	 *
	 * @param e the event to be processed
	 */
	@Override
	public void windowDeactivated(WindowEvent e) {

	}
}
