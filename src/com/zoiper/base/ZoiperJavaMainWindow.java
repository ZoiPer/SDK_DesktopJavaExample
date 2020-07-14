package com.zoiper.base;

// location of the jar zdk.java\ZoiperJava\swt.jar
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;

import java.lang.Thread.UncaughtExceptionHandler;

import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.zoiper.zdk.*;
import com.zoiper.zdk.Configurations.*;
import com.zoiper.zdk.EventHandlers.*;
import com.zoiper.zdk.Types.*;
import com.zoiper.zdk.Types.Zrtp.*;


public class ZoiperJavaMainWindow implements UncaughtExceptionHandler, ContextEventsHandler, CallEventsHandler, AccountEventsHandler, SIPProbeEventsHandler {

	private Context ctx = null;
	private Account account = null;
	private AccountConfig regCfg = null;
	private Result UserId = null;
	private Map<String, Account> ActiveUsers = new HashMap<String, Account>();
	private Map<String, Call> ActiveCalls = new HashMap<String, Call>();
	private boolean updateCalls = false;
	private long callId = 0;
	private VideoForm activeVideo = null;

	protected Shell shlZoiperSdk;
	protected Shell OfflineActivationSh;

	String ActFoldPath = "";
	private Button btnAddUser;
	private Text tbUserName;
	private Text tbPassword;
	private Text tbServer;
	private Label label1;
	private Label label2;
	private Label label3;
	private List lbUsers;
	private Label label4;
	private Button btnRegister;
	private Button btnUnregister;
	private Button btnCreateCall;
	private Label lblCallee;
	private Text tbCallee;
	private Label label6;
	private Group grpBAccountState;
	private Text tbIsRegistered;
	private Label label5;
	private Label label7;
	private List lbActiveCalls;
	private Text tbCertUserName;
	private Text tbCertPassword;
	private Label label8;
	private Label label9;
	private Group grpSdkActivation;
	private Button btnCertActivate;
	private Button btnOfflineCertActivate;
	private Button btnHoldCall;
	private Button btnMuteCall;
	private Button btnAddToConference;
	private Button btnHangUp;
	private Group grpCallControl;
	private Button btnVideo;
	private Button btnMessage;
	private Button btnStartRecording;
	private Button btnStopRecording;
	private Group grpAddUser;
	private StyledText rtbRunLog;
	private Button btnOffActvFolder;
	private Button btnCreateFile;
	private CCombo cbTransportType;
	private CCombo cbProtocolType;
	private Text tbBlindTransfer;
	private Button btnBlindTransfer;
	private Button btnAttTransfer;
	private Button btnProbeSIP;
	private CCombo cbDebugLevel;

	//check boxes in Add User group
	private Button chPrivacy;
	private Button chSRTP;
	private Button chZRTP;
	private Button chFMTP;
	private Button chStun;
	private Button chPreconditions;
	private Button chRTCFeedback;
	private boolean Activated;

	/**
	 * Launch the application.
	 * @param args
	 */

	static ZoiperJavaMainWindow window;
	Properties prop = new Properties();

	public static void main(String[] args) {
		try {
			window = new ZoiperJavaMainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}

		window.dispose();
		System.exit(0);
	}

	// private void loadSettings() {
	// 	InputStream input = null;

	// 	try {

	// 		String home = System.getProperty("user.home");

	// 		input = new FileInputStream(home + "\\config.properties");

	// 		// load a properties file
	// 		prop.load(input);
			
	// 	} catch (IOException ex) {
	// 		ex.printStackTrace();
	// 	} finally {
	// 		if (input != null) {
	// 			try {
	// 				input.close();
	// 			} catch (IOException e) {
	// 				e.printStackTrace();
	// 			}
	// 		}
	// 	}
	// }

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlZoiperSdk.open();
		shlZoiperSdk.layout();
		while (!shlZoiperSdk.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Gracefully dispose the ZDK.
	 */
	public void dispose() {
		if (ctx != null) {
			ctx.stopContext();
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlZoiperSdk = new Shell();
		shlZoiperSdk.setSize(905, 750);
		shlZoiperSdk.setText("Zoiper SDK");

		grpAddUser = new Group(shlZoiperSdk, SWT.NONE);
		grpAddUser.setText("Add User");
		grpAddUser.setBounds(12, 175, 517, 280);

		grpBAccountState = new Group(shlZoiperSdk, SWT.NONE);
		grpBAccountState.setText("VoIP User");
		grpBAccountState.setBounds(535, 25, 343, 430);

		grpSdkActivation = new Group(shlZoiperSdk, SWT.NONE);
		grpSdkActivation.setText("SDK Activation");
		grpSdkActivation.setBounds(10, 10, 192, 163);

		grpCallControl = new Group(grpBAccountState, SWT.NONE);
		grpCallControl.setText("Call Control");
		grpCallControl.setBounds(84, 165, 251, 200);

		Label lblUser = new Label(grpSdkActivation, SWT.NONE);
		lblUser.setText("User:");
		lblUser.setBounds(10, 19, 55, 15);

		Label lblPassword = new Label(grpSdkActivation, SWT.NONE);
		lblPassword.setText("Password:");
		lblPassword.setBounds(10, 45, 55, 15);

		Label lblDebugLevel = new Label(grpSdkActivation, SWT.NONE);
		lblDebugLevel.setText("Debug Level:");
		lblDebugLevel.setBounds(10, 133, 68, 15);

		cbDebugLevel = new CCombo(grpSdkActivation, SWT.BORDER);
		cbDebugLevel.setItems(new String[] {"None", "Critical", "Error", "Warning", "Info", "Debug", "Stack"});
		cbDebugLevel.setBounds(95, 133, 80, 21);
		cbDebugLevel.select(5);

		tbCertUserName = new Text(grpSdkActivation, SWT.BORDER);
		tbCertUserName.setBounds(76, 14, 109, 21);

		tbCertPassword = new Text(grpSdkActivation, SWT.BORDER);
		tbCertPassword.setBounds(76, 41, 109, 21);

		tbUserName = new Text(grpAddUser, SWT.BORDER);
		tbUserName.setBounds(75, 13, 176, 20);

		tbPassword = new Text(grpAddUser, SWT.BORDER);
		tbPassword.setText("Password");
		tbPassword.setBounds(75, 39, 176, 20);

		tbCallee = new Text(grpBAccountState, SWT.BORDER);
		tbCallee.setBounds(84, 50, 137, 20);

		try {
			//loadSettings();
			tbCertUserName.setText(prop.getProperty("tbCertUserName"));
			tbCertPassword.setText(prop.getProperty("tbCertPassword"));
			tbUserName.setText(prop.getProperty("sipuser"));
			tbCallee.setText(prop.getProperty("callee"));
		}
		catch (Exception ex)
		{
			
		}

		btnCertActivate = new Button(grpSdkActivation, SWT.NONE);
		btnCertActivate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					ctx = new Context();
				} catch (UnsatisfiedLinkError err) {
					String msg = err.getMessage();
					
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					err.printStackTrace(pw);
					String sStackTrace = sw.toString();
					
					JOptionPane.showMessageDialog(null, sStackTrace, "Native code library failed to load.\n: " + msg, JOptionPane.INFORMATION_MESSAGE);
				}
				
				ContextConfiguration config = null;
				try {
					//Start logging sessions
					File logFile = new File(System.getProperty("user.dir"), "log.txt");
					LoggingLevel logLevel = LoggingLevel.valueOf(cbDebugLevel.getText());
					ctx.logger().logOpen(logFile.getAbsolutePath(), null, logLevel, 0);

					config = ctx.configuration();
					if (null != config)
					{
						config.sipPort(44444);
						config.rtpPort(55555);

						//config.EchoCancellation(EchoCancellation.Software);
						//EchoCancellation ec = config.EchoCancellation();
						
						//config.EchoCancellation(EchoCancellation.Hardware);
						//ec = config.EchoCancellation();
					}

					String version = ctx.libraryVersion();

					ctx.setStatusListener(window);

					if (ctx != null && ActFoldPath == "")
					{
						ctx.activation().startSDK(null, tbCertUserName.getText(), tbCertPassword.getText());
					}
					else if(ctx != null && ActFoldPath != "")
					{
						ctx.activation().startSDK(ActFoldPath + "\\certificate", tbCertUserName.getText(), tbCertPassword.getText());
					}
					
					if(Activated != true)
					{
						LockGenerateContents();
					}

					Result res = ctx.startContext();

					ctx.videoControls().setFormat(VideoForm.resolution.width, VideoForm.resolution.height, 6);
				}
				catch(java.lang.NullPointerException ex)
				{
					String msg = ex.getMessage();
					
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					String sStackTrace = sw.toString();
					
					JOptionPane.showMessageDialog(null, sStackTrace, "InfoBox: " + msg, JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		btnCertActivate.setBounds(82, 68, 100, 25);
		btnCertActivate.setText("Activate");


		btnOfflineCertActivate = new Button(grpSdkActivation, SWT.NONE);
		btnOfflineCertActivate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					ctx = new Context();
				}catch (UnsatisfiedLinkError err) {
					String msg = err.getMessage();

					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					err.printStackTrace(pw);
					String sStackTrace = sw.toString();
					
					JOptionPane.showMessageDialog(null, sStackTrace, "Native code library failed to load.\n: " + msg, JOptionPane.INFORMATION_MESSAGE);
				}

				ContextConfiguration config = null;
				try {
					config = ctx.configuration();
					if (null != config)
					{
						config.sipPort(44444);
						config.rtpPort(55555);

						//config.EchoCancellation(EchoCancellation.Software);
						//EchoCancellation ec = config.EchoCancellation();
						
						//config.EchoCancellation(EchoCancellation.Hardware);
						//ec = config.EchoCancellation();
					}

					String version = ctx.libraryVersion();
					Result res = ctx.startContext();
					
					ctx.setStatusListener(window);
					//ContextAdvanced ctxAdv = ctx.Advanced();
					if (ctx != null)
					{
						OfflineActivationSh = new Shell();
						OfflineActivationSh.setText("Offline Activation");
						
						Label infoLabel = new Label(OfflineActivationSh, SWT.BORDER);
						infoLabel.setBounds(10, 10, 500, 150);
						infoLabel.setText(" 1. Select a path for the certificate to be holded \r\n" +
										  " 2. Press create certificate \r\n" +
										  " 3. Take the certificat and send it to register5@shop.zoiper \r\n" +
										  " 4. You will receive an xml file copy it to the folder you just took the certificate from \r\n" +
										  " 5. Press Activate");
						OfflineActivationSh.setSize(600, 300);
						btnOffActvFolder = new Button(OfflineActivationSh, SWT.NONE);
						btnOffActvFolder.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								final Text folderPath = new Text(OfflineActivationSh, SWT.BORDER);
								DirectoryDialog dlg = new DirectoryDialog(OfflineActivationSh);
								dlg.setFilterPath(folderPath.getText());
								dlg.setText("Folder for certificate");
								dlg.setMessage("Select a directory");
								ActFoldPath = dlg.open();
							}
							});
						btnOffActvFolder.setBounds(10,200,100,25);
						btnOffActvFolder.setText("Select Folder");
						btnCreateFile = new Button(OfflineActivationSh, SWT.NONE);
						btnCreateFile.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								if (ctx != null)
								{
									ctx.activation().createOfflineActivationFileSDK(ActFoldPath + "\\" + tbCertUserName.getText()  + ".certificate" ,tbCertUserName.getText(), tbCertPassword.getText());
								}
							}
							});
						btnCreateFile.setBounds(120,200,100,25);
						btnCreateFile.setText("Create File");
						OfflineActivationSh.open();
						//ctx.activation().startSDK(null, tbCertUserName.getText(), tbCertPassword.getText());
					}
				}
				catch(java.lang.NullPointerException ex)
				{
					String msg = ex.getMessage();

					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					ex.printStackTrace(pw);
					String sStackTrace = sw.toString();

					JOptionPane.showMessageDialog(null, sStackTrace, "InfoBox: " + msg, JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		btnOfflineCertActivate.setBounds(82, 100, 100, 25);
		btnOfflineCertActivate.setText("Offline Activation");
		
		btnAddUser = new Button(grpAddUser, SWT.NONE);
		btnAddUser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				account = ctx.accountProvider().createUserAccount();

				account.accountName(tbUserName.getText());

				regCfg = ctx.accountProvider().createAccountConfiguration();

				regCfg.userName(tbUserName.getText());
				regCfg.password(tbPassword.getText());
				
				if("SIP".equals(cbProtocolType.getText()))
				{
					regCfg.type(ProtocolType.SIP);
					regCfg.sip(ctx.accountProvider().createSIPConfiguration());

					regCfg.sip().transport(TransportType.valueOf(cbTransportType.getText()));
					
					regCfg.sip().domain(tbServer.getText());
					regCfg.sip().enablePrivacy(chPrivacy.getSelection());
					regCfg.sip().enableSRTP(chSRTP.getSelection());
					regCfg.sip().enableVideoFMTP(chFMTP.getSelection());
					regCfg.sip().enablePreconditions(chPreconditions.getSelection());

					if(chZRTP.getSelection())
					{
						ArrayList<ZRTPHashAlgorithm> hash = new ArrayList<ZRTPHashAlgorithm>();
						hash.add(ZRTPHashAlgorithm.s384);
						hash.add(ZRTPHashAlgorithm.s256);

						ArrayList<ZRTPCipherAlgorithm> cipher = new ArrayList<ZRTPCipherAlgorithm>();
						cipher.add(ZRTPCipherAlgorithm.cipher_aes3);
						cipher.add(ZRTPCipherAlgorithm.cipher_aes2);
						cipher.add(ZRTPCipherAlgorithm.cipher_aes1);

						ArrayList<ZRTPAuthTag> auth = new ArrayList<ZRTPAuthTag>();
						auth.add(ZRTPAuthTag.hs80);
						auth.add(ZRTPAuthTag.hs32);

						ArrayList<ZRTPKeyAgreement> keyAgreement = new ArrayList<ZRTPKeyAgreement>();
						keyAgreement.add(ZRTPKeyAgreement.dh3k);
						keyAgreement.add(ZRTPKeyAgreement.dh2k);
						keyAgreement.add(ZRTPKeyAgreement.ec38);
						keyAgreement.add(ZRTPKeyAgreement.ec25);

						ArrayList<ZRTPSASEncoding> sasEncoding = new ArrayList<ZRTPSASEncoding>();
						sasEncoding.add(ZRTPSASEncoding.sasb256);
						sasEncoding.add(ZRTPSASEncoding.sasb32);

						regCfg.sip().zrtp(ctx.accountProvider().createZRTPConfiguration());
						regCfg.sip().zrtp().enableZRTP(chZRTP.getSelection());
						regCfg.sip().zrtp().hash(hash);
						regCfg.sip().zrtp().cipher(cipher);
						regCfg.sip().zrtp().keyAgreement(keyAgreement);
						regCfg.sip().zrtp().sasEncoding(sasEncoding);
						regCfg.sip().zrtp().auth(auth);
						regCfg.sip().zrtp().cacheExpiry(-1);
					}

					if(chStun.getSelection())
					{
						regCfg.sip().stun(ctx.accountProvider().createStunConfiguration());
						regCfg.sip().stun().stunEnabled(chStun.getSelection());
						regCfg.sip().stun().stunServer("stun.zoiper.com");
						regCfg.sip().stun().stunPort(3478);
						regCfg.sip().stun().stunRefresh(30000);
					}

					if(chRTCFeedback.getSelection())
					{
						regCfg.sip().rtcpFeedback(RTCPFeedbackType.Compatibility); // Include AVP and AVPF video media profiles in the SDP for backward compatibility
					}
					else
					{
						regCfg.sip().rtcpFeedback(RTCPFeedbackType.Off); // Include only AVP video media profile in the SDP
					}

				}
				else if("IAX".equals(cbProtocolType.getText()))
				{
					ctx.addProtocol(ProtocolType.IAX, 4569);
					regCfg.type(ProtocolType.IAX);
					regCfg.iax(ctx.accountProvider().createIAXConfiguration());
					regCfg.iax().host(tbServer.getText());
				}

				ArrayList<AudioVideoCodecs> codecs = new ArrayList<AudioVideoCodecs>();
				codecs.add(AudioVideoCodecs.OPUS_WIDE);
				codecs.add(AudioVideoCodecs.PCMU);
				// This is for the video call
				codecs.add(AudioVideoCodecs.vp8);

				account.mediaCodecs(codecs);
				account.setStatusEventListener(window);
				account.configuration(regCfg);

				UserId = account.createUser();

				ActiveUsers.put(regCfg.userName(), account);
				lbUsers.add(regCfg.userName());
			}
		});
		btnAddUser.setText("Add User");
		btnAddUser.setBounds(40, 241, 75, 23);

		btnProbeSIP = new Button(grpAddUser, SWT.NONE);
		btnProbeSIP.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Account probingAccount = ctx.accountProvider().createUserAccount();
				
				if(probingAccount != null)
				{
					probingAccount.setProbeEventListener(window);
					probingAccount.probeSipTransport(tbServer.getText(), "", tbUserName.getText(), tbUserName.getText(), tbPassword.getText());
				}
			}
		});
		btnProbeSIP.setText("Probe SIP");
		btnProbeSIP.setBounds(135, 241, 75, 23);


		tbServer = new Text(grpAddUser, SWT.BORDER);
		tbServer.setText("sip4.zoiper.com");
		tbServer.setBounds(75, 65, 176, 20);

		label1 = new Label(grpAddUser, SWT.NONE);
		label1.setText("User Name:");
		label1.setBounds(6, 16, 63, 13);

		label2 = new Label(grpAddUser, SWT.NONE);
		label2.setText("Password:");
		label2.setBounds(6, 42, 56, 13);

		label3 = new Label(grpAddUser, SWT.NONE);
		label3.setText("Server:");
		label3.setBounds(6, 68, 41, 13);

		lbUsers = new List(grpAddUser, SWT.NONE);
		lbUsers.setBounds(260, 32, 242, 108);

		label4 = new Label(grpAddUser, SWT.NONE);
		label4.setText("VoIP Users List:");
		label4.setBounds(257, 16, 82, 13);

		cbTransportType = new CCombo(grpAddUser, SWT.BORDER);
		cbTransportType.setItems(new String[] {"NA", "UDP", "TCP", "TLS"});
		cbTransportType.setBounds(75, 91, 176, 21);
		cbTransportType.select(1);

		Label lblTransport = new Label(grpAddUser, SWT.NONE);
		lblTransport.setText("Transport:");
		lblTransport.setBounds(6, 95, 63, 17);

		cbProtocolType = new CCombo(grpAddUser, SWT.BORDER);
		cbProtocolType.setItems(new String[] {"SIP", "IAX"});
		cbProtocolType.setBounds(75, 120, 176, 21);
		cbProtocolType.select(0);

		Label lblProtocolType = new Label(grpAddUser, SWT.NONE);
		lblProtocolType.setText("Protocol:");
		lblProtocolType.setBounds(6, 120, 63, 13);

		chPrivacy = new Button(grpAddUser, SWT.CHECK);
		chPrivacy.setBounds(10, 175, 55, 23);
		chPrivacy.setText("Privacy");

		chSRTP = new Button(grpAddUser, SWT.CHECK);
		chSRTP.setBounds(80, 175, 50, 23);
		chSRTP.setText("SRTP");

		chZRTP = new Button(grpAddUser, SWT.CHECK);
		chZRTP.setBounds(140, 175, 50, 23);
		chZRTP.setText("ZRTP");

		chFMTP = new Button(grpAddUser, SWT.CHECK);
		chFMTP.setBounds(200, 175, 50, 23);
		chFMTP.setText("FMTP");

		chStun = new Button(grpAddUser, SWT.CHECK);
		chStun.setBounds(10, 210, 55, 23);
		chStun.setText("Stun");

		chPreconditions = new Button(grpAddUser, SWT.CHECK);
		chPreconditions.setBounds(80, 210, 90, 23);
		chPreconditions.setText("Preconditions");

		chRTCFeedback = new Button(grpAddUser, SWT.CHECK);
		chRTCFeedback.setBounds(180, 210, 86, 23);
		chRTCFeedback.setText("RTCFeedback");

		btnRegister = new Button(grpBAccountState, SWT.NONE);
		btnRegister.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Account acc = GetActiveUserAccount();
				if (acc != null)
				{
					Result result = acc.registerAccount();
					System.out.print(result.text());
				}
			}
		});
		btnRegister.setBounds(146, 19, 75, 23);
		btnRegister.setText("Register");

		btnUnregister = new Button(grpBAccountState, SWT.NONE);
		btnUnregister.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Account acc = GetActiveUserAccount();
				if (acc != null)
				{
					Result result = acc.unRegister();
				}
			}
		});
		btnUnregister.setBounds(227, 19, 75, 23);
		btnUnregister.setText("Unregister");

		btnCreateCall = new Button(grpBAccountState, SWT.NONE);
		btnCreateCall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Account acc = ActiveUsers.get(lbUsers.getSelection()[0]);
				Call activeCall = acc.createCall(tbCallee.getText(), true, false);
				activeCall.setCallStatusListener(window);
				ActiveCalls.put(tbCallee.getText(), activeCall);
				updateCalls = true;
			}
		});
		btnCreateCall.setBounds(227, 48, 75, 23);
		btnCreateCall.setText("Create Call");

		lblCallee = new Label(grpBAccountState, SWT.NONE);
		lblCallee.setText("Callee:");
		lblCallee.setBounds(6, 53, 39, 13);

		label6 = new Label(grpBAccountState, SWT.NONE);
		label6.setText("Transoprt:");
		label6.setBounds(6, 94, 55, 13);

		btnStartRecording = new Button(grpCallControl, SWT.NONE);
		btnStartRecording.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Call activeCall = GetActiveCall();
				if (activeCall != null)
				{
					String home = System.getProperty("user.home");
					String file = home + "\\call_record.wav";
					activeCall.recordFileName(file);
					activeCall.startRecording();
				}
			}
		});
		btnStartRecording.setBounds(6, 48, 75, 23);
		btnStartRecording.setText("Record");
		
		btnStopRecording = new Button(grpCallControl, SWT.NONE);
		btnStopRecording.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Call activeCall = GetActiveCall();
				if (activeCall != null)
				{
					activeCall.stopRecording();
				}
			}
		});
		btnStopRecording.setBounds(87, 48, 75, 23);
		btnStopRecording.setText("Stop Record");

		btnMessage = new Button(grpCallControl, SWT.NONE);
		btnMessage.setBounds(168, 78, 75, 23);
		btnMessage.setText("Message");

		btnVideo = new Button(grpCallControl, SWT.NONE);
		btnVideo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Call activeCall = GetActiveCall();
				if (activeCall != null) {
					activeCall.offerVideo();
				}
			}
		});
		btnVideo.setBounds(168, 19, 75, 23);
		btnVideo.setText("Video");

		btnHoldCall = new Button(grpCallControl, SWT.NONE);
		btnHoldCall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Call activeCall = GetActiveCall();
				if (activeCall != null)
				{
					activeCall.held(!activeCall.held());
					if (activeCall.held())
						btnHoldCall.setText("Unhold");
					else
						btnHoldCall.setText("Hold");
				}
			}
		});
		btnHoldCall.setBounds(6, 19, 75, 23);
		btnHoldCall.setText("Hold");

		btnMuteCall = new Button(grpCallControl, SWT.NONE);
		btnMuteCall.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Call activeCall = GetActiveCall();
				if (activeCall != null)
				{
					activeCall.muted(!activeCall.muted());
					if (activeCall.muted())
						btnMuteCall.setText("Unmute");
					else
						btnMuteCall.setText("Mute");
				}
			}
		});
		btnMuteCall.setBounds(168, 48, 75, 23);
		btnMuteCall.setText("Mute");

		btnHangUp = new Button(grpCallControl, SWT.NONE);
		btnHangUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String callString = lbActiveCalls.getSelection()[0];
				Call activeCall = GetActiveCall();
				if(activeCall != null)
				{
					activeCall.hangUp();
				}
				if (callString != null)
				{
					updateCalls = true;
					ActiveCalls.remove(callString);
				}
			}
		});
		btnHangUp.setBounds(87, 19, 75, 23);
		btnHangUp.setText("Hang Up");

		btnAddToConference = new Button(grpCallControl, SWT.NONE);
		btnAddToConference.setBounds(6, 78, 156, 23);
		btnAddToConference.setText("Add To Conference");

		tbBlindTransfer = new Text(grpCallControl ,SWT.BORDER);
		tbBlindTransfer.setBounds(6, 110, 110, 23);

		btnBlindTransfer = new Button(grpCallControl , SWT.NONE);
		btnBlindTransfer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Call activeCall = GetActiveCall();
				String transferee = tbBlindTransfer.getText();
				if (activeCall != null && transferee != "")
				{
					activeCall.blindTransfer(transferee);
				}
				else
				{
					UnavailableAction();
				}
			}
		});
		btnBlindTransfer.setBounds(130, 110, 113, 24);
		btnBlindTransfer.setText("Blind Transfer");

		btnAttTransfer = new Button(grpCallControl , SWT.NONE);
		btnAttTransfer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Call callOne = ActiveCalls.get(0);
				Call callTwo = ActiveCalls.get(1);
				if (callOne != null && callTwo != null)
				{
					callOne.held(!callOne.held());
					callTwo.held(!callTwo.held());
					callOne.attendedTransfer(callTwo);
				}
				else
				{
					UnavailableAction();
				}
			}
		});
		btnAttTransfer.setBounds(130, 140, 113, 24);
		btnAttTransfer.setText("Attended Transfer");

		label7 = new Label(grpBAccountState, SWT.NONE);
		label7.setText("Active Calls:");
		label7.setBounds(6, 81, 65, 13);

		lbActiveCalls = new List(grpBAccountState, SWT.NONE);
		lbActiveCalls.setBounds(84, 77, 218, 82);

		tbIsRegistered = new Text(grpBAccountState, SWT.BORDER);
		tbIsRegistered.setBounds(84, 21, 56, 20);
		
		label5 = new Label(grpBAccountState, SWT.NONE);
		label5.setText("Is Registered:");
		label5.setBounds(6, 24, 72, 13);

		rtbRunLog = new StyledText(shlZoiperSdk, SWT.BORDER | SWT.READ_ONLY);
		rtbRunLog.setEnabled(false);
		rtbRunLog.setEditable(false);
		rtbRunLog.setBounds(12, 465, 866, 250);

	}

	public void LockGenerateContents()
	{
		synchronized(this) {
			try {
				this.wait();
			} catch(InterruptedException error) {
				error.printStackTrace();
			}
		}
	}

	public Account GetActiveUserAccount()
	{
		if (lbUsers.getSelectionCount() == 0)
		{
			UnavailableAction();
			return null;
		}
		return ActiveUsers.get(lbUsers.getSelection()[0]);
	}
	
	public Call GetActiveCall()
	{
		if (ActiveCalls.isEmpty())
			return null;
		if (lbActiveCalls.getSelectionCount() == 0)
		{
			UnavailableAction();
			return null;
		}
		return ActiveCalls.get(lbActiveCalls.getSelection()[0]);
	}

	public void AccountInfoRefresh(Account currentUser)
	{
		Call activeCall = GetActiveCall();
		if (updateCalls)
			lbActiveCalls.removeAll();
		boolean en = currentUser != null;
		//grpBAccountState.setEnabled(en);

		if(en)
		{
			tbIsRegistered.setText(currentUser.registrationStatus().name());
			//foreach (var call in voipUser.ActiveCalls.Values)
			//{
			//	lbActiveCalls.Items.Add(call);
			//}
		}
		else
		{
			tbIsRegistered.setText("");
		}
		if (!ActiveCalls.isEmpty())
		{
			grpCallControl.setEnabled(true);
			if (updateCalls)
			{
				for (Map.Entry<String, Call> entry: ActiveCalls.entrySet())
				{
					lbActiveCalls.add(entry.getKey());
				}
				//if (activeCall != null)
				//	lbActiveCalls.SelectedItem = activeCall;
			}
		}
		else
		{
			//grpCallControl.setEnabled(false);
		}
		updateCalls = false;
	}
	
	public void UnavailableAction()
	{
		final Runnable runnable =
			(Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
		if (runnable != null) runnable.run();
	}
	
	public void OnZoiperEvent(final String eventText)
	{
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				rtbRunLog.append(eventText);
				rtbRunLog.append("\n");
				//rtbRunLog.add(eventText);
				AccountInfoRefresh(GetActiveUserAccount());
			}
		});
	}
	
	@Override
	public void onContextSecureCertStatus(Context context, SecureCertData secureCert) {
		OnZoiperEvent("onContextSecureCertStatus ActualNameList: "  + secureCert.actualNameList());
	}

	@Override
	public void onCallStatusChanged(Call call, CallStatus status) {
		OnZoiperEvent("OnCallStatusChanged: " + status.lineStatus().name());
	}

	@Override
	public void onCallExtendedError(Call call, ExtendedError message) {
		OnZoiperEvent("OnCallExtendedError: " + message);
	}

	@Override
	public void onCallNetworkStatistics(Call call, NetworkStatistics networkStatistics) {
		//OnZoiperEvent("OnCallNetworkStatistics TotalOutputBytes: " + networkStatistics.totalOutputBytes());
	}

	@Override
	public void onCallNetworkQualityLevel(Call call, int CallChannel, int QualityLevel) {
		OnZoiperEvent("OnCallNetworkQualityLevel CallChannel: " + CallChannel + " QualityLevel: " + QualityLevel);
	}

	@Override
	public void onCallSecurityLevelChanged(Call call, CallMediaChannel channel, CallSecurityLevel level) {
		OnZoiperEvent("OnCallSecurityLevelChanged channel: " + channel.name() + " level: " + level.name());
	}

	@Override
	public void onCallDTMFResult(Call call, Result lResult) {
		OnZoiperEvent("OnCallDTMFResult: " + lResult);
	}

	@Override
	public void onCallTransferSucceeded(Call call) {
		OnZoiperEvent("OnCallTransferSucceeded");
	}

	@Override
	public void onCallTransferFailure(Call call, ExtendedError errorCode) {
		OnZoiperEvent("OnCallTransferFailure errorCode: " + errorCode);
	}

	@Override
	public void onCallTransferStarted(Call call, String name, String number, String URI) {
		OnZoiperEvent("OnCallTransferStarted name: " + name + " number: " + number + " URI: " + URI);
	}

	@Override
	public void onAccountStatusChanged(Account account, AccountStatus status, int statusCode) {
		OnZoiperEvent("OnAccountStatusChanged Status: " + status.name() + " Code: " + statusCode);
	}

	@Override
	public void onAccountRetryingRegistration(Account account, int isRetrying, int inSeconds) {
		OnZoiperEvent("AccountRetryingRegistration isRetrying: " + isRetrying + " inSeconds: " + inSeconds);
	}

	@Override
	public void onAccountIncomingCall(Account account, Call call) {
		//creating new display to show the window with instructions for activation
		call.ringing(); // send the SIP 180 Ringing! Some servers "don't like" when this response is missing!

		Display display = new Display();
		Shell sh = new Shell(display);
		call.setCallStatusListener(window);
		OnZoiperEvent("OnAccountIncomingCall: ");
		MessageBox messageBox = new MessageBox(sh, SWT.YES | SWT.NO);
		messageBox.setMessage("Accept call?");
		messageBox.setText("Accept call?");
		int response = messageBox.open();
		if (response == SWT.YES)
		{
			Result res = call.acceptCall();
			if((ResultCode.Ok == res.code()) && !(ActiveCalls.containsKey(call)))
			{
				ActiveCalls.put(call.calleeName(), call);
			}
		}
		else {
			call.hangUp();
		}
		display.dispose();
	}

	@Override
	public void onAccountChatMessageReceived(Account account, String pPeer, String pContent) {
		OnZoiperEvent("AccountChatMessageReceived: " + pPeer + " message: " + pContent);
	}

	@Override
	public void onAccountExtendedError(Account account, ExtendedError message) {
		OnZoiperEvent("OnAccountExtendedError: " + message);
	}

	@Override
	public void onAccountUserSipOutboundMissing(Account account) {
		OnZoiperEvent("OnAccountUserSipOutboundMissing: ");
	}

	@Override
	public void onAccountCallOwnershipChanged(Account account, Call call, OwnershipChange action) {
		OnZoiperEvent("OnAccountCallOwnershipChanged action: " + action.name());
	}

	@Override
	public void onContextActivationCompleted(Context context, ActivationResult activationResult) {
		if(activationResult.status() == ActivationStatus.Success)
		{
			synchronized(this) {
				Activated = true;
				notify();
			}
		}
	}

	@Override
	public void onProbeError(Account account, ProbeState curState, ExtendedError error) {
		OnZoiperEvent("onProbeError curState: " + curState + ", error= " + error.message());
	}

	@Override
	public void onProbeState(Account account, ProbeState newState) {
		OnZoiperEvent("onProbeState newState: " + newState);
	}

	@Override
	public void onProbeSuccess(Account account, TransportType transport) {
		OnZoiperEvent("onProbeSuccess newState: " + transport);
	}


	@Override
	public void onProbeFailed(Account account, ExtendedError error) {
		OnZoiperEvent("onProbeFailed error= " + error.message());
	}

	@Override
	public void onVideoOffered(Call call) {
		OnZoiperEvent("onVideoOffered");
		activeVideo = new VideoForm(call);
		call.acceptVideo(true);
	}

	@Override
	public void onVideoStarted(Call call, OriginType origin) {
		OnZoiperEvent("onVideoStarted: origin= " + origin);

		Thread t = new Thread() {
			@Override
			public void run() {
				activeVideo.start();
			}
		};
		t.setName("video call");
		t.setDaemon(true);
		t.setUncaughtExceptionHandler(this);
		t.start();
	}

	@Override
	public void onVideoStopped(Call call, OriginType origin) {
		OnZoiperEvent("onVideoStopped: origin= " + origin);
		activeVideo.stop();
	}

	@Override
	public void onVideoCameraChanged(Call call) {
		OnZoiperEvent("onVideoCameraChanged");
	}

	@Override
	public void onVideoFormatSelected(Call call, OriginType dir, int width, int height, float fps) {
		OnZoiperEvent("onVideoStarted: direction= " + dir + ", width= " + width + ", height= " + height + ", fps= " + fps);
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.err.println(String.format("Exception in thread %s", t.getName()));
		e.printStackTrace();
	}
}
