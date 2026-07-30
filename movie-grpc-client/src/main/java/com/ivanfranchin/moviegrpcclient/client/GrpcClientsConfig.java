package com.ivanfranchin.moviegrpcclient.client;

import com.ivanfranchin.moviegrpcclient.proto.MovieServerGrpc;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.ImportGrpcClients;

@Configuration
@ImportGrpcClients(
    target = "movie",
    types = {MovieServerGrpc.MovieServerBlockingStub.class})
public class GrpcClientsConfig {}
