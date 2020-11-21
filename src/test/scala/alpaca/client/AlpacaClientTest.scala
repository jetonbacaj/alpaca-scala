package alpaca.client
import alpaca.service.{Config, ConfigService}
import cats._
import org.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, WordSpec}

class AlpacaClientTest
    extends WordSpec
    with BeforeAndAfterEach
    with MockitoSugar {

  val hammockService: HammockService = mock[HammockService]
  val configService: ConfigService = mock[ConfigService]
  val mockConfig: Config = mock[Config]

  var alpacaClient: AlpacaClient = _
  override def beforeEach() {

    when(mockConfig.getBaseUrl).thenReturn("asdfasf")
    when(mockConfig.assets_url).thenReturn("asdfsadf")
    when(configService.getConfig).thenReturn(Eval.now {
      mockConfig
    })
    alpacaClient = new AlpacaClient(configService, hammockService)
    super.beforeEach() // To be stackable, must call super.beforeEach
  }

  "AlpacaClient" can {
    "getAccount " should {
      "invoke hammockService" in {
        alpacaClient.getAccount
      }
    }
  }

}
