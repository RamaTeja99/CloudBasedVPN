package com.example.backendvpn.service;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class WireGuardService {

    private static final Logger log = LoggerFactory.getLogger(WireGuardService.class);

    @Value("${ssh.private-key-path}")
    private String privateKeyPath;

    @Value("${ssh.username}")
    private String sshUsername;

    public String getServerPublicKey(String host) {
        JSch jsch = new JSch();
        Session session = null;

        try {
            jsch.addIdentity(privateKeyPath);
            session = jsch.getSession(sshUsername, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(10000);

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("cat /etc/wireguard/server-public.key");
            channel.connect();

            try (InputStream in = channel.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                return reader.readLine();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read server public key", e);
            } finally {
                channel.disconnect();
            }
        } catch (JSchException e) {
            log.error("SSH connection failed", e);
            throw new RuntimeException("SSH connection failed", e);
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
    }

    public void addClientPeer(String host, String clientPublicKey) {
        JSch jsch = new JSch();
        Session session = null;

        try {
            jsch.addIdentity(privateKeyPath);
            session = jsch.getSession(sshUsername, host, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(10000);

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("sudo wg set wg0 peer " + clientPublicKey + " allowed-ips 10.8.0.2/32");
            channel.connect();
            channel.disconnect();
        } catch (JSchException e) {
            log.error("SSH command execution failed", e);
            throw new RuntimeException("Failed to add client peer", e);
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
    }
}
