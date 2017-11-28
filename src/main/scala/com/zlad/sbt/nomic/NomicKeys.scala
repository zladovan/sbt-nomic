package com.zlad.sbt.nomic

import com.zlad.sbt.nomic.controller.NomicControllerKeys
import com.zlad.sbt.nomic.generator.NomicGeneratorKeys

trait NomicKeys extends NomicControllerKeys with NomicGeneratorKeys
object NomicKeys extends NomicKeys
