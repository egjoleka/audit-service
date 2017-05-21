package com.izettle.assignment;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngine;
import org.apache.cxf.transport.http_jetty.JettyHTTPServerEngineFactory;
import org.apache.log4j.BasicConfigurator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.izettle.assignment.api.AuditsControllerImpl;
import com.izettle.assignment.api.LoginControllerImpl;
import com.izettle.assignment.cassandra.CassandraUtils;
import com.izettle.assignment.crypto.PasswordCrypto;
import com.izettle.assignment.dao.IssuedBearerTokenDao;
import com.izettle.assignment.dao.LoginAuditsDao;
import com.izettle.assignment.dao.UserDao;
import com.izettle.assignment.service.AuditsService;
import com.izettle.assignment.service.LoginService;
import com.izettle.assignment.utils.BearerRandomGenerator;

public class IzettleAssignmentMain {

	static Logger cLogger = LoggerFactory.getLogger(IzettleAssignmentMain.class);
	public final static int REST_PORT = 9023;

	public static final SimpleTxGate txGate = new SimpleTxGate();

	public static void main(String[] args) throws Exception {
		try {
			BasicConfigurator.configure();

			final Configuration cfg = getCsConfig();
			String hostname = java.net.InetAddress.getLocalHost().getHostName();
			logAndPrint("Using encoding: " + System.getProperty("file.encoding"));
			logAndPrint("Establishing connection with Cassandra cluster");
			final Session cassandraSession = establishCassandraCommunication(cfg);

			logAndPrint("Starting REST services on http://" + hostname + ":" + REST_PORT + "/");
			final Server restServer = startRestServices(REST_PORT, cassandraSession);

			addGracefulShutdown(restServer);
			logAndPrint("*********************IZettleService started successfully*********************");
		} catch (Exception ex) {
			System.err.println("IZettleService failed to start");
			ex.printStackTrace();
			cLogger.error("IZettleService failed to start", ex);
			System.exit(-1);
		}
	}

	private static Configuration getCsConfig() throws Exception {
		final Configuration blackIceCfg = new PropertiesConfiguration("application.properties");
		return blackIceCfg;
	}

	public static Session establishCassandraCommunication(final Configuration cfg) {
		final Cluster cassandraCluster = CassandraUtils.initCommunicationWithCluster(cfg);
		final Session cassandraSession = cassandraCluster.connect();
		logAndPrint("Communication with Cassandra cluster Established");
		return cassandraSession;
	}

	public static Server startRestServices(final int serverPort, final Session cassandraSession) {
		hideServerHeader(serverPort);
		final JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		final JettyHTTPServerEngineFactory engineFactory = new JettyHTTPServerEngineFactory();
		JettyHTTPServerEngine engine = engineFactory.retrieveJettyHTTPServerEngine(serverPort);
		serveStaticContent(engine);
		final BearerRandomGenerator BearerRandomGenerator = new BearerRandomGenerator();
		final IssuedBearerTokenDao issuedBearerTokenDao = new IssuedBearerTokenDao(cassandraSession);
		final LoginAuditsDao loginAuditsDao = new LoginAuditsDao(cassandraSession);
		final UserDao userDao = new UserDao(cassandraSession);
		final PasswordCrypto passwordCrypto = new PasswordCrypto();
		final LoginService loginService = new LoginService(issuedBearerTokenDao, loginAuditsDao, userDao,
				BearerRandomGenerator, passwordCrypto);
		final AuditsService auditsService = new AuditsService(issuedBearerTokenDao, loginAuditsDao);
		sf.setResourceClasses(LoginControllerImpl.class);
		sf.setResourceProvider(new SingletonResourceProvider(new LoginControllerImpl(loginService)));
		sf.setResourceClasses(AuditsControllerImpl.class);
		sf.setResourceProvider(new SingletonResourceProvider(new AuditsControllerImpl(auditsService)));
		sf.setAddress("http://0.0.0.0:" + serverPort + "/");
		sf.setProvider(new JacksonJaxbJsonProvider());
		return sf.create();
	}

	private static void addGracefulShutdown(final Server server) {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				txGate.closeGate();
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					cLogger.warn("Sleep interrupted", e);
				}
				cLogger.info("Someone is trying to kill me...");
				server.stop();
				cLogger.info("Terminating the server...");
				server.destroy();
				cLogger.info("I am dying....I am Dead...GoodBye");
			}
		}));
	}

	private static void logAndPrint(String msg) {
		System.out.println(msg);
		cLogger.info(msg);
	}

	private static void hideServerHeader(final int serverPort) {
		try {
			final JettyHTTPServerEngineFactory engineFactory = new JettyHTTPServerEngineFactory();
			final JettyHTTPServerEngine engine = engineFactory.createJettyHTTPServerEngine(serverPort, "http");
			engine.setSendServerVersion(false);
		} catch (GeneralSecurityException | IOException e) {
			cLogger.warn("Unable to hide Server response header", e);
		}
	}

	// Attempting to add some user interface if time permits :)
	private static void serveStaticContent(final JettyHTTPServerEngine engine) {
		final List<Handler> handlers = new ArrayList<>();
		final ContextHandler contextHandler = new ContextHandler("/");
		final ResourceHandler resHandler = new StaticResourceHandler();
		final String absolutePath = IzettleAssignmentMain.class.getClassLoader().getResource("app").getPath();
		resHandler.setResourceBase(absolutePath);
		resHandler.setDirectoriesListed(false);
		resHandler.setEtags(true);
		contextHandler.setHandler(resHandler);
		handlers.add(contextHandler);
		engine.setHandlers(handlers);
	}
}
