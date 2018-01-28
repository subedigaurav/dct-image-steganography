package com.gasu.disteg.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Label;
import javax.swing.JButton;
import java.io.IOException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = -2552314263639842178L;
	private JPanel contentPane;
		
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainWindow() {
		
		//Custom Fonts
		Font font=null;
		try {
			font= Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Stackyard.ttf"));
			font=font.deriveFont(Font.BOLD, 20);
		} catch (FontFormatException | IOException e1) {
			e1.printStackTrace();
		}
		

		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		setLocationRelativeTo(null);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		setBounds(100, 100, 599, 463);
		contentPane = new JPanel();
		contentPane.setForeground(new Color(0, 0, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnInitialize= new JButton("Sender Module");
		btnInitialize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnInitialize.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("deprecation")
			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				SenderModule senderModule=new SenderModule();
				senderModule.setLocationRelativeTo(null);
				senderModule.show();
			}
		});
		btnInitialize.setBounds(247, 161, 124, 38);
		contentPane.add(btnInitialize);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
			}
		});
		btnExit.setBounds(247, 297, 127, 38);
		contentPane.add(btnExit);
		
		JButton btnReceiverM= new JButton("Receiver Module");
		btnReceiverM.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				ReceiverModule receiverModule=new ReceiverModule();
				receiverModule.setLocationRelativeTo(null);
				receiverModule.show();
			}
		});
		btnReceiverM.setBounds(247, 228, 124, 40);
		contentPane.add(btnReceiverM);
		
		Label label = new Label("Copyright \u00A9 GADS. All rights reserved.");
		label.setAlignment(Label.CENTER);
		label.setForeground(new Color(255, 255, 255));
		label.setBackground(new Color(0, 0, 102));
		label.setBounds(0, 441, 599, 22);
		contentPane.add(label);
		
		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(new Color(0, 0, 102));
		titlePanel.setBounds(0, 0, 599, 104);
		contentPane.add(titlePanel);
		titlePanel.setLayout(null);
		
		JLabel lblDISText = new JLabel("Digital Image Steganography");
		lblDISText.setHorizontalAlignment(SwingConstants.CENTER);
		lblDISText.setBounds(0, 0, 599, 104);
		titlePanel.add(lblDISText);
		lblDISText.setForeground(new Color(255, 255, 255));
		lblDISText.setBackground(new Color(0, 0, 102));
		lblDISText.setFont(new Font("Ikaros Sans Regular", Font.PLAIN, 40));
	}
}
