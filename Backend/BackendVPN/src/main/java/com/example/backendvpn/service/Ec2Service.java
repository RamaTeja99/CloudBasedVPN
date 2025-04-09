package com.example.backendvpn.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
public class Ec2Service {

    private static final Logger log = LoggerFactory.getLogger(Ec2Service.class);
    private final Ec2Client ec2;

    @Value("${aws.security-group}")
    private String securityGroup;

    @Value("${aws.ami-id}")
    private String amiId;

    @Value("${aws.instance-type}")
    private String instanceType;

    public Ec2Service(Ec2Client ec2) {
        this.ec2 = ec2;
    }

    public String launchInstance() {
        String userData = """
            #!/bin/bash
            apt-get update -y
            apt-get install -y wireguard resolvconf
            umask 077
            wg genkey | tee /etc/wireguard/server-private.key | wg pubkey > /etc/wireguard/server-public.key
            cat > /etc/wireguard/wg0.conf <<EOF
            [Interface]
            PrivateKey = $(cat /etc/wireguard/server-private.key)
            Address = 10.8.0.1/24
            ListenPort = 51820
            PostUp = iptables -A FORWARD -i wg0 -j ACCEPT; iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
            PostDown = iptables -D FORWARD -i wg0 -j ACCEPT; iptables -t nat -D POSTROUTING -o eth0 -j MASQUERADE
            EOF
            echo "net.ipv4.ip_forward=1" >> /etc/sysctl.conf
            sysctl -p
            systemctl enable wg-quick@wg0
            systemctl start wg-quick@wg0
            """;

        RunInstancesRequest request = RunInstancesRequest.builder()
                .imageId(amiId)
                .instanceType(InstanceType.fromValue(instanceType))
                .minCount(1)
                .maxCount(1)
                .securityGroupIds(securityGroup)
                .userData(Base64.getEncoder().encodeToString(userData.getBytes()))
                .build();

        try {
            RunInstancesResponse response = ec2.runInstances(request);
            String instanceId = response.instances().get(0).instanceId();

            // Wait for instance to be running
            waitForInstanceRunning(instanceId);
            return instanceId;
        } catch (Ec2Exception e) {
            log.error("Failed to launch instance", e);
            throw new RuntimeException("Failed to launch EC2 instance", e);
        }
    }

    private void waitForInstanceRunning(String instanceId) {
        boolean running = false;
        while (!running) {
            try {
                DescribeInstancesResponse response = ec2.describeInstances(
                        DescribeInstancesRequest.builder().instanceIds(instanceId).build());

                InstanceStateName state = response.reservations().get(0)
                        .instances().get(0).state().name();

                running = state == InstanceStateName.RUNNING;
                if (!running) {
                    TimeUnit.SECONDS.sleep(5);
                }
            } catch (Exception e) {
                log.error("Error waiting for instance", e);
                throw new RuntimeException("Instance status check failed", e);
            }
        }
    }

    public String getInstancePublicIp(String instanceId) {
        DescribeInstancesRequest request = DescribeInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        DescribeInstancesResponse response = ec2.describeInstances(request);
        return response.reservations().get(0).instances().get(0).publicIpAddress();
    }
}
