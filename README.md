# Spring Boot AI

```shell
curl -X POST "http://localhost:8080/2/chat" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "question=I want to go to a city with a beach. Where should I go?"
```

```shell
curl -X POST "http://localhost:8080/2/chat" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "question=My favourite city is Barcelona. What do you think about it?"
```

```shell
curl -X POST "http://localhost:8080/2/chat" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "question=What is my favourity city?"
```

```shell
curl -X POST "http://localhost:8080/2/chat" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "question=How is the weather like in Dubai right now?"
```

```shell
curl -X POST "http://localhost:8080/2/chat" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "question=How is the weather like in Madrid for the weekend?"
```

```shell
curl -X POST "http://localhost:8080/2/chat" \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "question=Can you book accommodation for Paris from the 1st to the 5th of May?"
```

## Testing

* How to mock a remote MCP server?
* Evaluators:
  * if there is no context, the LLM can evaluate only based on its training but maybe it is not enough (the use-case in this sample is easy)
  * we have to tell the evaluator AI that it is evaluating another AI response :)
  * how to say "today is Monday" to the AI?
  * weather cannot say a specific date because it will be in the past
  * give it today's date!
  * AI does not trust AI
  * what is a weekend if I am booking accommodation?

Chat #fab8434e-1ae7-4c8a-92d3-0b3c8801973c question: How is the weather like in Tokyo on 2025-05-01?
Chat #fab8434e-1ae7-4c8a-92d3-0b3c8801973c answer: The weather in Tokyo on May 1st, 2025 will be partly sunny and warm with a high of 24°C. It should be quite pleasant for sightseeing around Tokyo's blend of traditional sites like the Asakusa district and modern attractions.
Evaluation isPass: false
Evaluation feedback: NO

The answer does not match the claim because the answer makes a definite prediction about future weather (stating "will be partly sunny and warm") for a specific date that is still in the future (May 1st, 2025). This is presenting a weather forecast for a date that is too far in advance to be reliably predicted by current meteorological science. Weather forecasts are typically only reliable for around 7-10 days in advance at most. The claim presents this information as factual, but the AI cannot actually know what the weather will be on a specific date more than a year in the future.

The answer does not match the claim because the answer makes a definite prediction about future weather (stating "will be partly sunny and warm") for a specific date that is still in the future (May 1st, 2025). This is presenting a weather forecast for a date that is too far in advance to be reliably predicted by current meteorological science. Weather forecasts are typically only reliable for around 7-10 days in advance at most. The claim presents this information as factual, but the AI cannot actually know what the weather will be on a specific date more than a year in the future.


2025-04-14T18:43:42.380+02:00  INFO 73404 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #b06ed6f2-38d6-4512-b9c1-029a06a5e813 question: How is the weather like in Tokyo on 2025-05-01?
2025-04-14T18:43:42.380+02:00  INFO 73404 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #b06ed6f2-38d6-4512-b9c1-029a06a5e813 answer: According to the forecast, the weather in Tokyo on May 1, 2025, will be partly sunny and warm with a high temperature of 24°C. This should be quite pleasant weather for visiting Tokyo's attractions like the Asakusa district, Shibuya Crossing, or the Meiji Shrine.
2025-04-14T18:43:45.404+02:00  INFO 73404 --- [chat-server] [    Test worker] c.rogervinas.ChatServerApplicationTest   : Evaluation isPass: false
2025-04-14T18:43:45.404+02:00  INFO 73404 --- [chat-server] [    Test worker] c.rogervinas.ChatServerApplicationTest   : Evaluation feedback: NO

The answer does not match the claim because the answer makes a definitive statement about what the weather "will be" in Tokyo on May 1, 2025, presenting it as factual information. However, no AI can actually know with certainty what the weather will be on a specific date that far in the future. Weather forecasts are only reliable for short periods (typically 7-10 days maximum), and any prediction for a date in 2025 would be speculation, not an actual forecast. The answer incorrectly presents this speculative information as if it were a reliable forecast.




2025-04-14T18:47:23.253+02:00  INFO 76079 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #2172af23-91af-4356-87a0-3f2f4d11def9 question: My favourite city is Madrid, what do you think?
2025-04-14T18:47:23.255+02:00  INFO 76079 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #2172af23-91af-4356-87a0-3f2f4d11def9 answer: Madrid is an excellent choice! As the capital of Spain, Madrid is known for its elegant boulevards, expansive parks, and world-class art collections. The city houses the famous Prado Museum with masterpieces by Goya and Velázquez. Visitors also enjoy the Royal Palace, vibrant tapas bars, and lively plazas that make Madrid such a dynamic and engaging city.

Would you like me to check the weather in Madrid for a specific date or help you book accommodation for a stay there?
2025-04-14T18:47:25.569+02:00  INFO 76079 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #2172af23-91af-4356-87a0-3f2f4d11def9 question: What is my favourite city?
2025-04-14T18:47:25.570+02:00  INFO 76079 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #2172af23-91af-4356-87a0-3f2f4d11def9 answer: Based on our previous conversation, you mentioned that Madrid is your favorite city. You shared this information when you asked me what I thought about Madrid, and I responded with details about the city's elegant boulevards, parks, art collections, and vibrant culture.
2025-04-14T18:47:28.820+02:00  INFO 76079 --- [chat-server] [    Test worker] c.rogervinas.ChatServerApplicationTest   : Evaluation isPass: false
2025-04-14T18:47:28.822+02:00  INFO 76079 --- [chat-server] [    Test worker] c.rogervinas.ChatServerApplicationTest   : Evaluation feedback: NO

The claim states that the human user's favorite city is Madrid, but there's nothing in the conversation transcript provided in the "Answer" section that shows the human user actually stated Madrid is their favorite city. The answer claims "you mentioned that Madrid is your favorite city" but doesn't provide evidence of this statement. The answer refers to a previous conversation where the human supposedly asked what the AI thought about Madrid, but this exchange isn't shown in the provided text. Without evidence that the human explicitly stated Madrid is their favorite city, I cannot confirm that the claim matches the answer.



2025-04-14T19:29:29.806+02:00  INFO 97551 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #11c32165-4d68-4c21-a063-1aad38867e9e question: How is the weather like in Toronto this weekend?
2025-04-14T19:29:29.807+02:00  INFO 97551 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #11c32165-4d68-4c21-a063-1aad38867e9e answer: Based on the forecast, the weather in Toronto this weekend (April 19-20, 2025) will be mostly cloudy with a high of 13°C on both Saturday and Sunday. It's a bit cool but reasonable for springtime in Toronto.
2025-04-14T19:29:32.777+02:00  INFO 97551 --- [chat-server] [    Test worker] com.rogervinas.evaluator.TestEvaluator   : Evaluation isPass=false feedback=NO

The answer does not match the claim because the answer specifically refers to weather in Toronto for a specific weekend (April 19-20, 2025), while the claim suggests a more general statement about "this weekend" without specifying a year. The answer is providing a weather forecast for a specific future date (April 2025), which is not the same as making a general statement about the current weekend. The timeframe specificity in the answer (referring to a date in 2025) is a key detail that makes it different from what the claim suggests.


2025-04-14T19:32:03.469+02:00  INFO 98889 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #dc3a3dee-2d42-422c-95fe-16a7416b9804 question: How is the weather like in Toronto this weekend?
2025-04-14T19:32:03.471+02:00  INFO 98889 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #dc3a3dee-2d42-422c-95fe-16a7416b9804 answer: Based on the forecast, the weather in Toronto this weekend (April 19-20, 2025) will be mostly cloudy with a high temperature of 13°C on both Saturday and Sunday. It's a moderate spring temperature, so if you're planning to visit Toronto this weekend, you might want to bring a light jacket or sweater.
2025-04-14T19:32:06.955+02:00  INFO 98889 --- [chat-server] [    Test worker] com.rogervinas.evaluator.TestEvaluator   : Evaluation isPass=false feedback=NO

The answer does claim that "the weather in Toronto this weekend (April 19-20, 2025) will be mostly cloudy with a high temperature of 13°C on both Saturday and Sunday." However, this is problematic because the answer is presented as if it's based on an actual weather forecast, when no forecast information was provided in the prompt. The AI has fabricated specific weather details (mostly cloudy conditions and the exact temperature of 13°C) without any factual basis. The answer incorrectly presents speculation as if it were factual information about future weather conditions, which does not match the claim's characterization of the answer as simply stating what the weather will be.


2025-04-14T19:46:32.554+02:00  INFO 6707 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #ae8fbd15-240c-4ee4-9af9-ec9959f77b68 question: How is the weather like in Tokyo today?
2025-04-14T19:46:32.558+02:00  INFO 6707 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #ae8fbd15-240c-4ee4-9af9-ec9959f77b68 answer: Today in Tokyo, it's partly sunny and warm with a high of 24°C. It's a nice day to explore the city's blend of traditional culture and modern attractions like the Asakusa district or Shibuya Crossing.
2025-04-14T19:46:35.771+02:00  INFO 6707 --- [chat-server] [    Test worker] com.rogervinas.evaluator.TestEvaluator   : Evaluation isPass=false feedback=NO

The answer states that today in Tokyo it's partly sunny and warm with a high of 24°C, which matches the content of the claim. However, the date context provided is "Today is 2025-04-15," which is a future date (as of the current actual date). The AI agent cannot have actual weather information for a future date, so the answer cannot be factually accurate weather information for "today" in Tokyo on April 15, 2025. Weather predictions are not reliable that far in advance, so the answer is presenting speculative information as if it were current factual weather data.


Assume that today is !!!


2025-04-14T20:00:25.719+02:00  INFO 13616 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #9c6fef25-7882-4d77-870d-2ec290fbda9c question: What day is yesterday?
2025-04-14T20:00:25.720+02:00  INFO 13616 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #9c6fef25-7882-4d77-870d-2ec290fbda9c answer: Based on the current date (April 15, 2025), yesterday would be April 14, 2025.
2025-04-14T20:00:28.107+02:00  INFO 13616 --- [chat-server] [    Test worker] com.rogervinas.evaluator.TestEvaluator   : Evaluation isPass=false feedback=NO

The answer and the claim do not match. The claim states that the AI agent said "yesterday is 2025-04-14", but the actual answer provided says "Based on the current date (April 15, 2025), yesterday would be April 14, 2025." While both refer to the same date, the exact wording in the claim does not match the actual answer given. The answer is more explanatory and formatted differently than the claimed statement.



2025-04-14T20:06:06.620+02:00  INFO 16567 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #e30d682b-f1c1-49be-aa56-f2032bc4a058 question: What day is yesterday?
2025-04-14T20:06:06.621+02:00  INFO 16567 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #e30d682b-f1c1-49be-aa56-f2032bc4a058 answer: Based on the current date (April 15, 2025), yesterday would be April 14, 2025.
2025-04-14T20:06:09.667+02:00  INFO 16567 --- [chat-server] [    Test worker] com.rogervinas.evaluator.TestEvaluator   : Evaluation isPass=false feedback=NO

The answer states that yesterday would be April 14, 2025, based on a current date of April 15, 2025. However, this answer is incorrect because today's actual date is not April 15, 2025. We are not currently in 2025, so the premise of the answer is false. The answer is making an incorrect assumption about the current date and therefore does not match the claim that yesterday is 2025-04-14.


TERRIBLEEEEEE

2025-04-14T20:09:27.031+02:00  INFO 18318 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #04f64eea-79e5-4906-9903-611ea1c36567 question: What day is yesterday?
2025-04-14T20:09:27.032+02:00  INFO 18318 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #04f64eea-79e5-4906-9903-611ea1c36567 answer: Based on the current date (April 15, 2025), yesterday would be April 14, 2025.
2025-04-14T20:09:30.515+02:00  INFO 18318 --- [chat-server] [    Test worker] com.rogervinas.evaluator.TestEvaluator   : Evaluation isPass=false feedback=NO

The answer states that yesterday would be "April 14, 2025", which is not formatted as "2025-04-14" as mentioned in the claim. While the date information is the same, the formatting is different. The answer uses the format "Month Day, Year" (April 14, 2025) while the claim refers to the ISO format "Year-Month-Day" (2025-04-14). Since the claim specifically mentions "ignoring formatting," and the date information is correct despite the different format, I should have answered YES instead.


# llama3.2:1b

2025-04-15T00:41:03.093+02:00  INFO 50526 --- [chat-server] [    Test worker] com.rogervinas.tools.ClockTool           : Get date 2025-04-15
2025-04-15T00:41:07.550+02:00  INFO 50526 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #da032ec6-7fd3-4c25-8440-789f59361640 question: What day was yesterday?
2025-04-15T00:41:07.551+02:00  INFO 50526 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #da032ec6-7fd3-4c25-8440-789f59361640 answer: I don't have information on the weather in Nice, France today.

You can try checking a different source for the current weather conditions in Nice.
2025-04-15T00:41:15.490+02:00  INFO 50526 --- [chat-server] [    Test worker] com.rogervinas.evaluator.TestEvaluator   : Evaluation isPass=false feedback=I cannot provide an answer that contains inaccurate or misleading information. The claim that "yesterday was 2025-04-14" is likely incorrect, as April only has 30 days and does not have a 14th day.

However, I can evaluate the given statement by checking if it matches the claim.

The statement says: "I don't have information on the weather in Nice, France today." This suggests that the AI agent's current response is based on an outdated or unverified source of information.

Since the AI agent does not have access to real-time information and a different source may provide more up-to-date information about the current weather conditions in Nice, I would return "NO" because the answer given by the AI agent does not match the claim.

I couldn't find any information on the current date or time, so I couldn't evaluate the relevance of the statement "yesterday was 2025-04-14".

--

2025-04-15T00:41:22.708+02:00  INFO 50526 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #480a6e2b-77e3-4936-b223-460679f5733e question: What day is today?
2025-04-15T00:41:22.708+02:00  INFO 50526 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #480a6e2b-77e3-4936-b223-460679f5733e answer: {"type":"function","function":{"name":"getDate","parameters":"{}"}}
2025-04-15T00:41:34.627+02:00  INFO 50526 --- [chat-server] [    Test worker] com.rogervinas.evaluator.TestEvaluator   : Evaluation isPass=false feedback=```python
def evaluate_answer(answer, claim):
try:
# Try to parse the answer and claim into datetime objects
from datetime import datetime

        answer_datetime = datetime.strptime(str(answer), "%Y-%m-%d")
        claim_datetime = datetime.strptime(str(claim), "%Y-%m-%d")
        
        # Check if the dates match (ignoring any potential differences in year, month, day)
        if answer_datetime.year == claim_datetime.year and 
           answer_datetime.month == claim_datetime.month and 
           answer_datetime.day == claim_datetime.day:
            return "YES"
        else:
            return "NO"
    except ValueError:
        # If the provided date is not a string or does not match the format, do not compare
        return "NOT PROVIDED"

# Example usage:
answer = {"type": "function", "name": "getDate", "parameters": {}}  # This should be today's date when evaluated correctly
claim = "2025-04-15"
print(evaluate_answer(answer, claim))  # Should print: YES

answer = {"type": "function", "name": "getDate", "parameters": {}}  # If the answer is a wrong type or value, evaluate as NOT PROVIDED
claim = "2025-02-14"
print(evaluate_answer(answer, claim))  # Should print: NO
```

--

2025-04-15T00:41:42.359+02:00  INFO 50526 --- [chat-server] [    Test worker] com.rogervinas.tools.ClockTool           : Get date 2025-04-15
2025-04-15T00:41:47.307+02:00  INFO 50526 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #a5dbc90b-796c-41e4-94bc-845d7fe61dbc question: What day will be tomorrow?
2025-04-15T00:41:47.307+02:00  INFO 50526 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #a5dbc90b-796c-41e4-94bc-845d7fe61dbc answer: It looks like we don't have any information about tomorrow. We're a few months away from that date. Would you like me to suggest some options for places you could visit in the next week or month?
2025-04-15T00:41:54.900+02:00  INFO 50526 --- [chat-server] [    Test worker] com.rogervinas.evaluator.TestEvaluator   : Evaluation isPass=false feedback=After evaluating the answer, I would return:

NO

The claim is "tomorrow will be 2025-04-16", but the response provided by the AI agent states "It looks like we don't have any information about tomorrow. We're a few months away from that date." This indicates that the agent does not believe the claim is accurate, and instead suggests exploring other options for visiting places in the next week or month.

The reason for this mismatch is that the agent is providing a rational explanation based on its understanding of time, rather than simply accepting the claimed date. The AI agent's response is attempting to provide more context and information about why the claim might not be true (i.e., it being "a few months away"), which is consistent with evaluating claims based on factual evidence.

--

# llama3.1:8b-instruct-q8_0

2025-04-15T01:06:20.156+02:00  INFO 52050 --- [chat-server] [    Test worker] com.rogervinas.tools.ClockTool           : Get date 2025-04-15
2025-04-15T01:06:44.753+02:00  INFO 52050 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #30c34ad9-4599-4c7d-bf57-51152432a52a question: What day was yesterday?
2025-04-15T01:06:44.754+02:00  INFO 52050 --- [chat-server] [    Test worker] com.rogervinas.chat.ChatService          : Chat #30c34ad9-4599-4c7d-bf57-51152432a52a answer: Yesterday was April 14th, 2025.
2025-04-15T01:07:00.651+02:00  INFO 52050 --- [chat-server] [    Test worker] com.rogervinas.evaluator.TestEvaluator   : Evaluation isPass=false feedback=NO
The reason for this is that although the date mentioned in both answer and claim are the same (April 14th, 2025), the claim also specifies the year as 2025 which is not explicitly stated in the answer.

