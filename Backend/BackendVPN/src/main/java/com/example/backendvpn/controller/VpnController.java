package com.example.backendvpn.controller;

import com.example.backendvpn.service.Ec2Service;
import com.example.backendvpn.service.WireGuardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vpn")
public class VpnController {

    private static final Logger log = LoggerFactory.getLogger(VpnController.class);

    private final Ec2Service ec2Service;
    private final WireGuardService wireGuardService;

    public VpnController(Ec2Service ec2Service, WireGuardService wireGuardService) {
        this.ec2Service = ec2Service;
        this.wireGuardService = wireGuardService;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startVpn(@RequestParam String userId) {
        try {
            // 1. Launch EC2 instance
            String instanceId = ec2Service.launchInstance();
            String publicIp = ec2Service.getInstancePublicIp(instanceId);

            // 2. Get server public key
            String serverPublicKey = wireGuardService.getServerPublicKey(publicIp);

            // 3. Generate client keys (simplified - use proper crypto in production)
            String clientPrivateKey = generatePrivateKey();
            String clientPublicKey = generatePublicKey(clientPrivateKey);

            // 4. Add client to WireGuard
            wireGuardService.addClientPeer(publicIp, clientPublicKey);

            // 5. Return client config
            String config = String.format("""
                [Interface]
                PrivateKey = %s
                Address = 10.8.0.2/24
                DNS = 1.1.1.1
                
                [Peer]
                PublicKey = %s
                Endpoint = %s:51820
                AllowedIPs = 0.0.0.0/0
                PersistentKeepalive = 25
                """,
                clientPrivateKey,
                serverPublicKey,
                publicIp
            );

            return ResponseEntity.ok(config);
        } catch (Exception e) {
            log.error("VPN start failed", e);
            return ResponseEntity.internalServerError()
                    .body("Failed to start VPN: " + e.getMessage());
        }
    }

    private String generatePrivateKey() {
        // In production, use proper cryptographic libraries
        return "simulated-private-key-" + System.currentTimeMillis();
    }

    private String generatePublicKey(String privateKey) {
        // In production, derive from private key
        return "simulated-public-key-" + privateKey.hashCode();
    }
}
