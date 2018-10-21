package com.zelkatani.routing

import com.zelkatani.noSessionExists
import spark.ModelAndView
import spark.Request
import spark.Response

object IndexRouting {
    fun index(req: Request, res: Response): ModelAndView {
        val model = mutableMapOf<String, Any>("not_auth" to req.noSessionExists())
        return ModelAndView(model, "index.ftl")
    }
}