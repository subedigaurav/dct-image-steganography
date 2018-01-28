package com.gasu.disteg.gui;

import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import com.gasu.disteg.misc.ImageToArray;

import javax.swing.SwingConstants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.awt.Panel;
import java.awt.Color;
import java.awt.Font;

public class ReceiverModule extends JFrame {

	private static final long serialVersionUID = -7650175698297135928L;
	private JPanel contentPane;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReceiverModule frame = new ReceiverModule();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	public ReceiverModule() {
		setResizable(false);
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
		
		JLabel lblReceiverModule = new JLabel("Receiver Module");
		lblReceiverModule.setForeground(new Color(255, 255, 255));
		panel.add(lblReceiverModule);
		lblReceiverModule.setFont(new Font("Ikaros Sans Regular", Font.PLAIN, 40));
		lblReceiverModule.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel panelSourceImage = new JPanel();
		panelSourceImage.setBorder(new BevelBorder(BevelBorder.LOWERED, new Color(51, 255, 51), null, new Color(255, 0, 0), null));
		panelSourceImage.setBounds(4, 132, 237, 248);
		contentPane.add(panelSourceImage);
		panelSourceImage.setLayout(null);
		
		JLabel lblStegoImage = new JLabel("Stego Image");
		lblStegoImage.setFont(new Font("Ikaros Sans Regular", Font.PLAIN, 18));
		lblStegoImage.setBounds(0, 0, 250, 250);
		panelSourceImage.add(lblStegoImage);
		lblStegoImage.setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton btnSelectStegoImage = new JButton("Select Stego Image");
		btnSelectStegoImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser imageChooser=new JFileChooser();
				imageChooser.setCurrentDirectory(new File  (System.getProperty("user.home") + System.getProperty("file.separator")+ "Pictures"));
				if(imageChooser.showOpenDialog(btnSelectStegoImage)==JFileChooser.APPROVE_OPTION){
					File file=imageChooser.getSelectedFile();
					ImageIcon imageIcon=new ImageIcon(imageChooser.getSelectedFile().getPath());
					Image image=imageIcon.getImage().getScaledInstance(lblStegoImage.getWidth(), lblStegoImage.getHeight(), java.awt.Image.SCALE_SMOOTH);
					imageIcon =new ImageIcon(image);
					lblStegoImage.setIcon(imageIcon);
					ImageToArray imageToArray=new ImageToArray();
				}
			}
		});
		btnSelectStegoImage.setFont(new Font("Ubuntu", Font.PLAIN, 12));
		btnSelectStegoImage.setBounds(4, 377, 237, 30);
		contentPane.add(btnSelectStegoImage);
		
		Panel sendPanel = new Panel();
		sendPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
				SenderModule send=new SenderModule();
				send.setLocationRelativeTo(null);
				send.show();
			}
		});
		sendPanel.setBackground(new Color(204, 0, 255));
		sendPanel.setBounds(311, 435, 288, 28);
		contentPane.add(sendPanel);
		
		JLabel lblSend = new JLabel("SEND");
		lblSend.setForeground(new Color(255, 255, 255));
		lblSend.setFont(new Font("Ikaros Sans Regular", Font.PLAIN, 23));
		sendPanel.add(lblSend);
		
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
		homePanel.setBounds(0, 435, 313, 28);
		contentPane.add(homePanel);
		
		JLabel lblHome = new JLabel("HOME");
		lblHome.setForeground(new Color(255, 255, 255));
		lblHome.setHorizontalAlignment(SwingConstants.CENTER);
		lblHome.setFont(new Font("Ikaros Sans Regular", Font.PLAIN, 23));
		homePanel.add(lblHome);
	}
}
