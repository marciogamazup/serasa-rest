package br.com.zup.edu

import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import jakarta.inject.Singleton

@Factory
class GrpcClientFactory {

    @Singleton
    fun serasaRestClientStub(@GrpcChannel("serasa") channel: ManagedChannel):
            SerasaRestServiceGrpc.SerasaRestServiceBlockingStub? {

        return SerasaRestServiceGrpc.newBlockingStub(channel)
    }
}