package com.gasu.disteg.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.gasu.disteg.misc.ImageToArray;

import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.border.BevelBorder;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SenderModule extends JFrame {

	private static final long serialVersionUID = 7591746518286453472L;
	private JPanel contentPane;
	File file;
	int sted = 0;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SenderModule frame = new SenderModule();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SenderModule() {
		//Basic JFrame Properties
		setResizable(false);
		int masd = 445;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		setLocationRelativeTo(null);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		setBounds(100, 100, 599, 463);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		ImageIcon imageIcon=new ImageIcon("powerBtn.png");
		Image image=imageIcon.getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
		imageIcon =new ImageIcon(image);
		
		JLabel lblExit = new JLabel("");
		lblExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
			}
		});
		lblExit.setHorizontalAlignment(SwingConstants.CENTER);
		lblExit.setBounds(553, 0, 50, 50);
		lblExit.setIcon(imageIcon);
		contentPane.add(lblExit);
		
		Panel panel = new Panel();
		panel.setBackground(new Color(200, 92, 92));
		panel.setBounds(0, 0, 599, 55);
		contentPane.add(panel);
		
		JLabel lblSenderModule = new JLabel("Sender Module");
		lblSenderModule.setForeground(new Color(255, 255, 255));
		panel.add(lblSenderModule);
		lblSenderModule.setFont(new Font("Ikaros Sans Regular", Font.PLAIN, 40));
		lblSenderModule.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panelSourceImage = new JPanel();
		panelSourceImage.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(51, 255, 51), null, new Color(255, 0, 0), null));
		panelSourceImage.setBounds(4, 132, 237, 248);
		contentPane.add(panelSourceImage);
		panelSourceImage.setLayout(null);
		
		JLabel lblSourceImage = new JLabel("Source Image");
		lblSourceImage.setFont(new Font("Ikaros Sans Regular", Font.PLAIN, 18));
		lblSourceImage.setBounds(0, 0, 250, 250);
		panelSourceImage.add(lblSourceImage);
		lblSourceImage.setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton btnSelectFromFile = new JButton("Select from File");
		btnSelectFromFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser imageChooser=new JFileChooser();
				imageChooser.setCurrentDirectory(new File  (System.getProperty("user.home") + System.getProperty("file.separator")+ "Pictures"));
				if(imageChooser.showOpenDialog(btnSelectFromFile)==JFileChooser.APPROVE_OPTION){
					file=imageChooser.getSelectedFile();
					ImageIcon imageIcon=new ImageIcon(imageChooser.getSelectedFile().getPath());
					Image image=imageIcon.getImage().getScaledInstance(lblSourceImage.getWidth(), lblSourceImage.getHeight(), java.awt.Image.SCALE_SMOOTH);
					imageIcon =new ImageIcon(image);
					lblSourceImage.setIcon(imageIcon);
					ImageToArray imageToArray=new ImageToArray();
				}
			}
		});
		btnSelectFromFile.setFont(new Font("Ubuntu", Font.PLAIN, 12));
		btnSelectFromFile.setBounds(4, 377, 237, 30);
		contentPane.add(btnSelectFromFile);
		
		Panel homePanel = new Panel();
		homePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				MainWindow main=new MainWindow();
				main.setLocationRelativeTo(null);
				main.show();
			}
		});
		homePanel.setBackground(new Color(153, 0, 0));
		homePanel.setBounds(0, 435, 301, 28);
		contentPane.add(homePanel);
		
		Panel receiverPanel = new Panel();
		receiverPanel.setBackground(new Color(0, 0, 153));
		receiverPanel.setBounds(298, 435, 301, 28);
		contentPane.add(receiverPanel);
		
		JLabel lblReceive = new JLabel("RECEIVE");
		lblReceive.setForeground(new Color(255, 255, 255));
		lblReceive.setHorizontalAlignment(SwingConstants.CENTER);
		lblReceive.setFont(new Font("Ikaros Sans Regular", Font.PLAIN, 23));
		receiverPanel.add(lblReceive);
		
		JLabel lblHome = new JLabel("HOME");
		lblHome.setForeground(new Color(255, 255, 255));
		lblHome.setHorizontalAlignment(SwingConstants.CENTER);
		lblHome.setFont(new Font("Ikaros Sans Regular", Font.PLAIN, 23));
		homePanel.add(lblHome);
	}
}
