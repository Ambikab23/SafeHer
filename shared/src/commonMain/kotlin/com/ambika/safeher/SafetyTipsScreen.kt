
package com.ambika.safeher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val TipsPink = Color(0xFFB81D52)
private val TipsBackground = Color(0xFFFDF7F9)
private val TipsText = Color(0xFF30252A)
private val TipsMuted = Color(0xFF88777E)
private val TipsBorder = Color(0xFFEADCE2)

private data class SafetyTip(
    val number: String,
    val title: String,
    val description: String
)

@Composable
fun SafetyTipsScreen(
    onBack: () -> Unit
) {
    val tips = listOf(
        SafetyTip(
            "01",
            "Stay Aware of Your Surroundings",
            "Avoid distractions while walking. Keep an eye on the people and places around you, especially when travelling alone."
        ),
        SafetyTip(
            "02",
            "Share Your Location",
            "Let someone you trust know where you are going. Share your live location when travelling to an unfamiliar place."
        ),
        SafetyTip(
            "03",
            "Trust Your Instincts",
            "If a situation or person makes you uncomfortable, move to a safer and more public place. Your safety comes first."
        ),
        SafetyTip(
            "04",
            "Keep Emergency Contacts Ready",
            "Save the phone numbers of trusted friends and family members so you can contact them quickly when needed."
        ),
        SafetyTip(
            "05",
            "Plan Your Journey",
            "Prefer well-lit and populated routes. Check your transport arrangements before leaving and avoid isolated areas."
        ),
        SafetyTip(
            "06",
            "Use SOS in an Emergency",
            "If you feel unsafe, seek immediate help. Use the SafeHer SOS feature and contact local emergency services when necessary."
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TipsBackground)
            .safeDrawingPadding()
    ) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onBack,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = TipsPink
                )
            ) {
                Text(
                    "‹ Back",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Safety Tips",
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = TipsText
            )
        }

        HorizontalDivider(color = TipsBorder)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp,
                    vertical = 22.dp
                ),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {

            Text(
                text = "Your Safety Matters",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = TipsPink
            )

            Text(
                text = "Simple habits that can help you stay aware, confident and safe.",
                fontSize = 14.sp,
                color = TipsMuted,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            tips.forEach { tip ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, TipsBorder),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(17.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    color = Color(0xFFF9E8EE),
                                    shape = RoundedCornerShape(13.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tip.number,
                                color = TipsPink,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Text(
                                text = tip.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TipsText,
                                lineHeight = 21.sp
                            )

                            Text(
                                text = tip.description,
                                fontSize = 13.sp,
                                color = TipsMuted,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF9E8EE)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = "Remember",
                        color = TipsPink,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "You deserve to feel safe wherever you go. Never hesitate to ask for help when you need it.",
                        color = TipsText,
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TipsPink,
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Return to Home",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
