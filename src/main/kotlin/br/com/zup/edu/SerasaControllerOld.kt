package br.com.zup.edu

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import jakarta.inject.Inject

@Controller
class SerasaControllerOld(@Inject val gRpcClient: SerasaRestServiceGrpc.SerasaRestServiceBlockingStub){

    @Get("/api/serasa/clientes/verificar-situacao")
    fun verificar(@QueryValue cpf: String): SituacaoDoClienteResponse{

        val request = SituacaoDoClienteRequest.newBuilder()
            .setCpf(cpf)
            .build()



//        val situacao = // TODO: consumir API gRPC aqui


        val response = gRpcClient!!.verificarSituacaoDoCliente(request)

        return SituacaoDoClienteResponse(response)

//        return HttpResponse.ok(SituacaoNoSerasaResponse(cpf, situacao))
    }
}

data class SituacaoDoClienteResponse(
//    val cpf: String,
    val situacao: Situacao
)

enum class Situacao {
    SEM_INFORMACOES,
    REGULARIZADA,
    NAO_REGULARIZADA
}