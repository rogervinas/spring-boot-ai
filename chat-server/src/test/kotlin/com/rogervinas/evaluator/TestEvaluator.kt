package com.rogervinas.evaluator

import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.ChatClient.PromptUserSpec
import org.springframework.ai.evaluation.EvaluationRequest
import org.springframework.ai.evaluation.EvaluationResponse
import org.springframework.ai.evaluation.Evaluator

/**
 * @see org.springframework.ai.evaluation.RelevancyEvaluator
 * @see org.springframework.ai.evaluation.FactCheckingEvaluator
 */

open class TestEvaluator(
    private val chatClientBuilder: ChatClient.Builder,
    private val user: (EvaluationRequest, PromptUserSpec) -> Unit
) : Evaluator {

    private val logger = LoggerFactory.getLogger(TestEvaluator::class.java)

    override fun evaluate(evaluationRequest: EvaluationRequest): EvaluationResponse {
        val evaluationResponse = chatClientBuilder.build()
            .prompt()
            .user { userSpec -> user(evaluationRequest, userSpec) }
            .call()
            .content()

        return EvaluationResponse(
            evaluationResponse!!.contains("YES"),
            evaluationResponse,
            mutableMapOf<String, Any>()
        ).apply {
            logger.info("Evaluation isPass=${this.isPass} feedback=${this.feedback}")
        }
    }
}
