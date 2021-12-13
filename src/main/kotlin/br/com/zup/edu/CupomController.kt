package br.com.zup.edu

import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.exceptions.HttpStatusException
import jakarta.inject.Inject
import org.slf4j.LoggerFactory
import javax.validation.constraints.NotBlank

@Controller
class CupomController(
    @Inject
    val grpcClient: CupomGrpcServiceGrp.CupomGrpcServiceBlockingStub) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Get("/api/cupons/{cupom}")
    fun consultar(@NotBlank @PathVariable cupom: String): HttpResponse<Any> {

        try {

            LOGGER.info("Consultando `$cupom` informado no serviço externo")
            val response = grpcClient.consultar(
                CupomRequest.newBuilder().setCupom(cupom).build()
            )
            return HttpResponse.ok(CupomResponse(cupom, response.valor))

        } catch (e: StatusRuntimeException) {

            val statusCode = e.status.code
            val description = e.status.description

            if (statusCode == io.grpc.Status.Code.NOT_FOUND) {
                LOGGER.info("erro do `$cupom` não foi encontrado")
                throw HttpStatusException(HttpStatus.NOT_FOUND, description)
            }

            if (statusCode == io.grpc.Status.Code.INVALID_ARGUMENT) {
                LOGGER.info("erro do `$cupom` formato inválido")
                throw HttpStatusException(HttpStatus.BAD_REQUEST, description)
            }

            if (statusCode == io.grpc.Status.Code.FAILED_PRECONDITION) {

                LOGGER.info("O cupom `$cupom` está expirado ou já foi utilizado")

                val statusProto = StatusProto.fromThrowable(e)

                if (statusProto == null) {
                    throw HttpStatusException(HttpStatus.ALREADY_IMPORTED, description)
                }

                val anyDetails: Any = statusProto.detailsList.get(0)
                val errorDetails = anyDetails.unpack(ErrorDetails::class.java)

                throw HttpStatusException(HttpStatus.ALREADY_IMPORTED, "${errorDetails.code}: ${errorDetails.message}")
            }
            LOGGER.info("erro inexperado para o cupom `$cupom`")
            throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }
    }
}