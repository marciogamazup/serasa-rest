package br.com.zup.edu

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import jakarta.inject.Inject

@Controller
class SerasaController (@Inject val gRpcClient: SerasaRestServiceGrpc.SerasaRestServiceBlockingStub) {

    @Get("/api/serasa/clientes/verificar-situacao")
    fun verificar(@QueryValue cpf: String): HttpResponse<Any> {

        val request = SituacaoDoClienteRequest.newBuilder()
            .setCpf(cpf)
            .build()

        val response = gRpcClient.verificarSituacaoDoCliente(request)

        return HttpResponse.ok(SituacaoNoSerasaResponse(
            situacaoResponse = response.situacao.name)
        )
    }
}

data class SituacaoNoSerasaResponse(
    val situacaoResponse: String
)
