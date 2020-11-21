package alpaca.service

import alpaca.dto.Parameter

trait BaseService {

  def createTuples(arguments: Parameter*): Option[Array[(String, String)]] = {
    val args = arguments.flatMap(parameter => {
      parameter.value.map { parameterValue =>
        Tuple2.apply(parameter.name, parameterValue.toString)
      }
    })

    if (args.isEmpty) {
      None
    } else {
      Some(args.toArray)
    }
  }
}
