package io.quarkus.grpc.example.interceptors;

import io.quarkus.grpc.test.utils.VertxGRPCTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(VertxGRPCTestProfile.class)
class VertxHelloWorldEndpointTest extends HelloWorldEndpointTestBase {

}
