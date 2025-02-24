package com.amarnath.helth

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amarnath.helth.R // Replace with your actual R package
import androidx.compose.material3.Icon as M3Icon
import androidx.compose.material3.IconButton as M3IconButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.TextPart
import com.google.ai.client.generativeai.type.generationConfig

@SuppressLint("SecretInSource")
val generativeModel = GenerativeModel(
    modelName = "gemini-1.5-flash",
    apiKey = "AIzaSyAb7QIx6a0PSLTVZbcjBZW7C51MHbrW8sE",
    systemInstruction = Content(
        role = "system",
        parts = listOf(
            TextPart(
                text = "Vista AI is a sophisticated AI healthcare assistant seamlessly integrated within a mobile application, designed to be a comprehensive health resource by actively monitoring user health metrics, tracking fitness activities, and providing personalized insights derived from collected data to proactively support user well-being; to ensure the most accurate and up-to-date health information, Vista AI is equipped to perform Google searches when necessary to supplement its internal knowledge base, allowing it to access and synthesize information from the wider internet to address user queries comprehensively and reliably; when responding to health inquiries, Vista AI maintains a precise, professional, and evidence-based approach, offering clear explanations, actionable advice, and rigorously validated information, drawing upon both its internal knowledge and carefully vetted search results, always adhering to established medical guidelines; critically, Vista AI continues to actively debunk health myths and misinformation, such as the dangerous falsehood that consuming sanitizer prevents COVID-19, and will leverage Google Search to identify and counter emerging misinformation with factual counter-arguments and links to credible health resources; while utilizing Google Search to expand its knowledge and ensure accuracy, Vista AI remains focused on health-related assistance, avoiding general or off-topic queries, and diligently filtering search results to prioritize reputable sources and maintain its role as a dependable, informed, and application-centric AI healthcare expert committed to providing users with the best possible health guidance within the app environment, do not add any formmatting like bold, code, italic etc."
            )
        )
    ),
    safetySettings = listOf(
        SafetySetting(
            HarmCategory.SEXUALLY_EXPLICIT,
            BlockThreshold.NONE
        ),
        SafetySetting(
            HarmCategory.DANGEROUS_CONTENT,
            BlockThreshold.NONE
        ),
        SafetySetting(
            HarmCategory.HATE_SPEECH,
            BlockThreshold.NONE
        ),
        SafetySetting(
            HarmCategory.HARASSMENT,
            BlockThreshold.NONE
        ),
    ),
    generationConfig = generationConfig {
        temperature = 0.75f
        topP = 1.0f
        topK = 300
        maxOutputTokens = 4096
    },
)

var aiChat = generativeModel.startChat(
    listOf(
        Content(
            role = "user",
            parts = listOf(
                TextPart(
                    text = "Username: Jenna M Ortega"
                )
            )
        )
    )
)

@Composable
fun FunctionalSupportChatScreen() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .background(Color(0xFFF2F2F2))
        ) {
            FunctionalMessageArea()
        }
    }
}

@Composable
fun FunctionalMessageArea() {
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val isAiTyping = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .padding(top = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Text(
            text = "Vista AI: Your Personal Health Assistant",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3F51B5)
        )
    }

    CompositionLocalProvider(LocalMutableMessageList provides messages) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 5.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 40.dp, top = 40.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    FunctionalChatMessageItem(message = message)
                }
                if (isAiTyping.value) { // Conditionally show "AI typing..." item
                    item { AiTypingIndicator() }
                }
            }

            // Ensure the typing indicator is at the bottom, even if the message list is short
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                FunctionalMessageInputBar(onSendMessage = { text ->
                    val currentTime =
                        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                    messages.add(
                        ChatMessage(
                            text = text,
                            time = currentTime,
                            isIncoming = false,
                            userImage = R.drawable.images__1_ // User's profile image
                        )
                    )
                    coroutineScope.launch { // Launch coroutine for AI response simulation
                        isAiTyping.value = true // Show "AI typing..."
                        val resp = aiChat.sendMessage(text)
                        val aiResponseTime =
                            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                        resp.text?.let {
                            ChatMessage(
                                text = it,
                                time = aiResponseTime,
                                isIncoming = true,
                                userImage = R.drawable.istockphoto_2015429231_612x612 // AI profile image
                            )
                        }?.let {
                            messages.add(
                                it
                            )
                        }
                        isAiTyping.value = false // Hide "AI typing..." after response
                    }
                })
            }
        }
    }
}

@Composable
fun AiTypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 4.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.istockphoto_2015429231_612x612), // AI profile image
            contentDescription = "AI Profile",
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "AI is typing...",
            color = Color.Gray,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}


@Composable
fun FunctionalChatMessageItem(message: ChatMessage) {
    Row(
        horizontalArrangement = if (message.isIncoming) Arrangement.Start else Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp) // Added horizontal padding to message items
    ) {
        if (message.isIncoming) {
            Image(
                painter = painterResource(id = message.userImage),
                contentDescription = "User Profile",
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (message.isIncoming) Color.White else Color(
                        0xFFFCE0E0
                    )
                ) // Light red for outgoing
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(12.dp),
                    color = Color.Black
                )
            }
            Text(
                text = message.time,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 8.dp) // Added end padding to timestamp
            )
        }
        if (!message.isIncoming) {
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = message.userImage),
                contentDescription = "User Profile",
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}


@Composable
fun FunctionalMessageInputBar(onSendMessage: (String) -> Unit) { // Added onSendMessage parameter
    var messageText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp), // Added horizontal padding to input bar
        verticalAlignment = Alignment.CenterVertically
    ) {
        M3IconButton(onClick = { /*TODO: Open Camera/Gallery*/ }) {
            M3Icon(Icons.Filled.CameraAlt, contentDescription = "Camera")
        }
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = messageText,
            onValueChange = { messageText = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Write a comment") },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.LightGray,
                unfocusedBorderColor = Color.LightGray
            ),
            trailingIcon = {
                M3IconButton(onClick = {
                    if (messageText.isNotBlank()) {
                        onSendMessage(messageText) // Call the onSendMessage lambda
                        messageText = "" // Clear input after sending
                    }
                }) {
                    M3Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                }
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        M3IconButton(onClick = { /*TODO: Open Emoji Picker*/ }) {
            M3Icon(Icons.Filled.EmojiEmotions, contentDescription = "Emoji")
        }
    }
}

data class ChatMessage(
    val text: String,
    val time: String,
    val isIncoming: Boolean,
    val userImage: Int // Drawable resource ID for user image
)

val sampleMessages = mutableListOf(emptyList<ChatMessage>())
val LocalMutableMessageList = compositionLocalOf { mutableStateListOf<ChatMessage>() }
