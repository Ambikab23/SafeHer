
package com.ambika.safeher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ambika.safeher.chat.ChatRepository
import com.ambika.safeher.chat.ChatScreen
import com.ambika.safeher.database.SafeHerDatabase
import kotlinx.coroutines.delay

// ----------------------------------------------------
// SAFEHER — RASPBERRY PINK DESIGN SYSTEM
// ----------------------------------------------------

private val Pink = Color(0xFFB81D52)
private val PinkDark = Color(0xFF941540)
private val PinkLight = Color(0xFFF9E8EE)
private val PinkPale = Color(0xFFFFF2F6)

private val Background = Color(0xFFFDF7F9)
private val White = Color(0xFFFFFFFF)
private val CardSoft = Color(0xFFFFFAFC)
private val Border = Color(0xFFEADCE2)

private val TextPrimary = Color(0xFF30252A)
private val TextSecondary = Color(0xFF88777E)

private val EmergencyRed = Color(0xFFD92F4F)
private val EmergencyTint = Color(0xFFFFE9EE)
private val SuccessGreen = Color(0xFF27866A)

private val BlueTint = Color(0xFFEDF3FA)
private val BlueDark = Color(0xFF456B91)

private val WarmTint = Color(0xFFF5F0E8)
private val WarmDark = Color(0xFF94784B)

private val LargeShape = RoundedCornerShape(25.dp)
private val CardShape = RoundedCornerShape(20.dp)
private val ButtonShape = RoundedCornerShape(15.dp)

// ----------------------------------------------------
// MAIN APP
// ----------------------------------------------------

@Composable
fun App(
    database: SafeHerDatabase,
    onRequestLocationPermission: (onResult: (Double?, Double?, Boolean) -> Unit) -> Unit = {},
    onRegisterBackHandler: (onBack: () -> Unit) -> (() -> Unit) = { _ -> {} },
    onExitApp: () -> Unit = {},
    onPrepareSms: (phoneNumber: String, message: String) -> Unit = { _, _ -> },
    onOpenAppSettings: () -> Unit = {}
) {
    val repository = remember(database) {
        EmergencyContactsRepository(database)
    }

    val chatRepository = remember(database) {
        ChatRepository(database)
    }

    var currentScreen by remember {
        mutableStateOf("welcome")
    }

    val goHome: () -> Unit = {
        currentScreen = "home"
    }

    DisposableEffect(currentScreen) {
        if (currentScreen == "welcome") {
            onDispose {}
        } else {
            val unregister = onRegisterBackHandler {
                when (currentScreen) {
                    "contacts", "chat", "safety" -> {
                        currentScreen = "home"
                    }
                    "home" -> onExitApp()
                }
            }

            onDispose {
                unregister()
            }
        }
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Pink,
            onPrimary = White,
            secondary = PinkDark,
            onSecondary = White,
            background = Background,
            surface = White,
            onSurface = TextPrimary,
            error = EmergencyRed,
            onError = White
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Background
        ) {
            when (currentScreen) {
                "welcome" -> WelcomeScreen {
                    currentScreen = "home"
                }

                "home" -> HomeScreen(
                    emergencyContactsRepository = repository,
                    onRequestLocationPermission = onRequestLocationPermission,
                    onPrepareSms = onPrepareSms,
                    onContactsClick = {
                        currentScreen = "contacts"
                    },
                    onChatClick = {
                        currentScreen = "chat"
                    },
                    onSafetyTipsClick = {
                        currentScreen = "safety"
                    },
                    onOpenAppSettings = onOpenAppSettings
                )

                "contacts" -> EmergencyContactsScreen(
                    repository = repository,
                    onBack = goHome
                )

                "chat" -> ChatScreen(
                    repository = chatRepository,
                    onBack = goHome
                )

                "safety" -> SafetyTipsScreen(
                    onBack = goHome
                )
            }
        }
    }
}

// ----------------------------------------------------
// REUSABLE COMPONENTS
// ----------------------------------------------------

@Composable
private fun Eyebrow(
    text: String,
    color: Color = TextSecondary
) {
    Text(
        text = text,
        fontSize = 10.sp,
        letterSpacing = 1.3.sp,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

@Composable
private fun FeatureIcon(
    symbol: String,
    background: Color = PinkLight,
    foreground: Color = Pink,
    size: androidx.compose.ui.unit.Dp = 48.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                background,
                RoundedCornerShape(15.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            fontSize = 25.sp,
            fontWeight = FontWeight.Medium,
            color = foreground
        )
    }
}

// ----------------------------------------------------
// WELCOME SCREEN
// ----------------------------------------------------

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF8F1742),
                        Pink,
                        Color(0xFFD84F7D)
                    )
                )
            )
            .safeDrawingPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(20.dp))

            // Shield-inspired logo
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .background(
                        White.copy(alpha = 0.16f),
                        RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(78.dp)
                        .background(
                            White,
                            RoundedCornerShape(25.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "♥",
                        fontSize = 43.sp,
                        fontWeight = FontWeight.Bold,
                        color = Pink
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "SAFEHER",
                fontSize = 32.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.ExtraBold,
                color = White
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "YOUR PERSONAL SAFETY COMPANION",
                fontSize = 9.sp,
                letterSpacing = 1.6.sp,
                fontWeight = FontWeight.Bold,
                color = White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(43.dp))

            Text(
                "Your safety,\nour priority.",
                fontSize = 37.sp,
                lineHeight = 45.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.8).sp,
                color = White,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Feel confident wherever life takes you. Stay prepared, stay connected and keep the people you trust close.",
                fontSize = 14.sp,
                lineHeight = 23.sp,
                color = White.copy(alpha = 0.88f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            WelcomeFeature(
                symbol = "SOS",
                title = "Emergency assistance",
                description = "Quick access when you need help"
            )

            Spacer(Modifier.height(12.dp))

            WelcomeFeature(
                symbol = "♡",
                title = "Trusted connections",
                description = "Keep your important contacts close"
            )

            Spacer(Modifier.height(12.dp))

            WelcomeFeature(
                symbol = "⌖",
                title = "Location support",
                description = "Access and share your location"
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = ButtonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = Pink
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Text(
                    "Get Started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    "→",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(17.dp))

            Text(
                "STAY AWARE. STAY CONNECTED. STAY SAFE.",
                fontSize = 9.sp,
                letterSpacing = 1.1.sp,
                fontWeight = FontWeight.SemiBold,
                color = White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun WelcomeFeature(
    symbol: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                White.copy(alpha = 0.14f),
                RoundedCornerShape(17.dp)
            )
            .padding(horizontal = 15.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(43.dp)
                .background(
                    White,
                    RoundedCornerShape(13.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                symbol,
                fontSize = if (symbol == "SOS") 12.sp else 23.sp,
                fontWeight = FontWeight.Bold,
                color = Pink
            )
        }

        Spacer(Modifier.width(13.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )

            Spacer(Modifier.height(3.dp))

            Text(
                description,
                fontSize = 11.sp,
                color = White.copy(alpha = 0.82f)
            )
        }
    }
}

// ----------------------------------------------------
// HOME DASHBOARD
// ----------------------------------------------------

@Composable
fun HomeScreen(
    emergencyContactsRepository: EmergencyContactsRepository,
    onRequestLocationPermission: (onResult: (Double?, Double?, Boolean) -> Unit) -> Unit,
    onPrepareSms: (phoneNumber: String, message: String) -> Unit,
    onContactsClick: () -> Unit,
    onChatClick: () -> Unit,
    onSafetyTipsClick: () -> Unit,
    onOpenAppSettings: () -> Unit
) {
    var showSosCountdown by remember {
        mutableStateOf(false)
    }

    var countdown by remember {
        mutableIntStateOf(5)
    }

    var showContactsDialog by remember {
        mutableStateOf(false)
    }

    var showLocationDialog by remember {
        mutableStateOf(false)
    }

    var showSosConfirmation by remember {
        mutableStateOf(false)
    }

    var savedContacts by remember {
        mutableStateOf<List<EmergencyContact>>(emptyList())
    }

    var locationCoordinates by remember {
        mutableStateOf<Pair<Double, Double>?>(null)
    }

    var locationStatus by remember {
        mutableStateOf("Location not requested yet.")
    }

    var isLocationLoading by remember {
        mutableStateOf(false)
    }

    var showLocationSettingsButton by remember {
        mutableStateOf(false)
    }

    val uriHandler = LocalUriHandler.current

    fun requestLocation() {
        locationCoordinates = null
        isLocationLoading = true
        locationStatus = "Getting your current location..."

        onRequestLocationPermission { latitude, longitude, permanentlyDenied ->
            isLocationLoading = false
            showLocationSettingsButton = permanentlyDenied

            if (latitude != null && longitude != null) {
                locationCoordinates = latitude to longitude
                locationStatus = "Your current location was retrieved successfully."
                showLocationSettingsButton = false
            } else {
                locationCoordinates = null
                locationStatus =
                    "Location unavailable. Please check your location settings."
            }
        }
    }

    fun activateSos() {
        savedContacts = emergencyContactsRepository.getAllContacts()
        showContactsDialog = true
        requestLocation()
    }

    LaunchedEffect(showSosCountdown) {
        if (showSosCountdown) {
            countdown = 5

            for (seconds in 5 downTo 1) {
                countdown = seconds
                delay(1000)
            }

            showSosCountdown = false
            activateSos()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
    ) {

        // TOP BRAND HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(horizontal = 21.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(43.dp)
                    .background(PinkLight, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "♥",
                    color = Pink,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "SAFEHER",
                    fontSize = 20.sp,
                    letterSpacing = 1.7.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Pink
                )

                Text(
                    "YOUR PERSONAL SAFETY COMPANION",
                    fontSize = 8.sp,
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .size(39.dp)
                    .background(PinkPale, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "S",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Pink
                )
            }
        }

        HorizontalDivider(color = Border)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {

            // GREETING
            Spacer(Modifier.height(25.dp))

            Eyebrow("WELCOME TO SAFEHER", Pink)

            Spacer(Modifier.height(7.dp))

            Text(
                "Hello, welcome back!",
                fontSize = 25.sp,
                lineHeight = 31.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.6).sp,
                color = TextPrimary
            )

            Spacer(Modifier.height(5.dp))

            Text(
                "Your safety matters. We're here for you.",
                fontSize = 13.sp,
                color = TextSecondary
            )

            // SOS HERO CARD
            Spacer(Modifier.height(22.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = LargeShape,
                colors = CardDefaults.cardColors(
                    containerColor = Pink
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Pink,
                                    Color(0xFF921641)
                                )
                            )
                        )
                        .padding(19.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Eyebrow(
                                "EMERGENCY SUPPORT",
                                White.copy(alpha = 0.78f)
                            )

                            Spacer(Modifier.height(7.dp))

                            Text(
                                "Are you safe?",
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )

                            Spacer(Modifier.height(5.dp))

                            Text(
                                "Get help from your trusted contacts.",
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = White.copy(alpha = 0.84f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .background(
                                    White.copy(alpha = 0.16f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "SOS",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = White
                            )
                        }
                    }

                    Spacer(Modifier.height(19.dp))

                    Button(
                        onClick = {
                            countdown = 5
                            showSosCountdown = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 53.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = White,
                            contentColor = Pink
                        )
                    ) {
                        Text(
                            "ACTIVATE SOS",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(Modifier.height(9.dp))

                    Text(
                        "5-second countdown  •  Cancel anytime",
                        fontSize = 10.sp,
                        color = White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // SAFETY TOOLS
            Spacer(Modifier.height(27.dp))

            Text(
                "Your safety tools",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(Modifier.height(5.dp))

            Text(
                "Everything you need, in one place.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardActionCard(
                    title = "Emergency Contacts",
                    description = "Your trusted people",
                    symbol = "♡",
                    iconBackground = PinkLight,
                    iconForeground = Pink,
                    onClick = onContactsClick,
                    modifier = Modifier.weight(1f)
                )

                DashboardActionCard(
                    title = "Emergency Chat",
                    description = "Stay connected",
                    symbol = "☏",
                    iconBackground = BlueTint,
                    iconForeground = BlueDark,
                    onClick = onChatClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardActionCard(
                    title = "Safety Tips",
                    description = "Helpful safety guidance",
                    symbol = "✧",
                    iconBackground = WarmTint,
                    iconForeground = WarmDark,
                    onClick = onSafetyTipsClick,
                    modifier = Modifier.weight(1f)
                )

                DashboardActionCard(
                    title = "My Location",
                    description = "Find your current location",
                    symbol = "⌖",
                    iconBackground = PinkPale,
                    iconForeground = PinkDark,
                    onClick = {
                        showLocationDialog = true
                        requestLocation()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // BOTTOM REMINDER
            Spacer(Modifier.height(22.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        PinkLight,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 15.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "♥",
                    fontSize = 21.sp,
                    color = Pink
                )

                Spacer(Modifier.width(11.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Your safety comes first",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PinkDark
                    )

                    Spacer(Modifier.height(3.dp))

                    Text(
                        "Stay aware. Stay connected. Stay safe.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(25.dp))
        }
    }

    // ------------------------------------------------
    // SOS COUNTDOWN DIALOG
    // ------------------------------------------------

    if (showSosCountdown) {
        AlertDialog(
            onDismissRequest = {
                showSosCountdown = false
            },
            title = {
                Text(
                    "SOS countdown",
                    fontWeight = FontWeight.Bold,
                    color = PinkDark
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Emergency assistance will be prepared in",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(20.dp))

                    Box(
                        modifier = Modifier
                            .size(108.dp)
                            .background(EmergencyTint, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            countdown.toString(),
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyRed
                        )
                    }

                    Spacer(Modifier.height(15.dp))

                    Eyebrow("SECONDS REMAINING", Pink)

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Cancel if you tapped SOS accidentally.",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSosCountdown = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Pink
                    ),
                    shape = ButtonShape
                ) {
                    Text(
                        "Cancel SOS",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            containerColor = White,
            shape = LargeShape
        )
    }

    // ------------------------------------------------
    // LOCATION DIALOG
    // ------------------------------------------------

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = {
                showLocationDialog = false
            },
            title = {
                Text(
                    "My location",
                    fontWeight = FontWeight.Bold,
                    color = PinkDark
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 330.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        locationStatus,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )

                    if (isLocationLoading) {
                        Spacer(Modifier.height(14.dp))

                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = Pink
                        )
                    }

                    locationCoordinates?.let { coordinates ->
                        Spacer(Modifier.height(16.dp))

                        LocationValue(
                            label = "LATITUDE",
                            value = coordinates.first.toString()
                        )

                        Spacer(Modifier.height(10.dp))

                        LocationValue(
                            label = "LONGITUDE",
                            value = coordinates.second.toString()
                        )

                        Spacer(Modifier.height(15.dp))

                        Button(
                            onClick = {
                                val mapsUrl =
                                    "https://www.google.com/maps/search/?api=1&query=${coordinates.first},${coordinates.second}"

                                uriHandler.openUri(mapsUrl)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Pink
                            ),
                            shape = ButtonShape
                        ) {
                            Text("Open in Google Maps")
                        }
                    }

                    if (showLocationSettingsButton) {
                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = onOpenAppSettings,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Pink
                            ),
                            shape = ButtonShape
                        ) {
                            Text("Open App Settings")
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "Your location is requested when you open this screen or tap Refresh.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLocationDialog = false
                    }
                ) {
                    Text(
                        "Close",
                        color = Pink
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isLocationLoading,
                    onClick = {
                        requestLocation()
                    }
                ) {
                    Text(
                        "Refresh",
                        color = Pink
                    )
                }
            },
            containerColor = White,
            shape = LargeShape
        )
    }

    // ------------------------------------------------
    // SOS CONTACT SELECTION DIALOG
    // ------------------------------------------------

    if (showContactsDialog) {
        AlertDialog(
            onDismissRequest = {
                showContactsDialog = false
            },
            title = {
                Column {
                    Eyebrow("EMERGENCY ASSISTANCE", EmergencyRed)

                    Spacer(Modifier.height(5.dp))

                    Text(
                        "SOS activated",
                        fontWeight = FontWeight.Bold,
                        color = PinkDark
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 370.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (savedContacts.isEmpty()) {
                        Text(
                            "No emergency contacts found. Please add contacts in the Emergency Contacts section.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            lineHeight = 21.sp
                        )
                    } else {
                        Text(
                            "Choose a trusted contact to prepare your SOS message.",
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )

                        Spacer(Modifier.height(14.dp))

                        savedContacts.forEach { contact ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 11.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = CardSoft
                                ),
                                shape = RoundedCornerShape(17.dp),
                                border = BorderStroke(1.dp, Border)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        FeatureIcon(
                                            symbol = "♡",
                                            background = PinkLight,
                                            foreground = Pink
                                        )

                                        Spacer(Modifier.width(12.dp))

                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                contact.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = TextPrimary,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            Spacer(Modifier.height(3.dp))

                                            Text(
                                                contact.relationship,
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )

                                            Text(
                                                contact.phoneNumber,
                                                fontSize = 12.sp,
                                                color = TextPrimary
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(13.dp))

                                    Button(
                                        onClick = {
                                            val locationLink =
                                                locationCoordinates?.let {
                                                    "https://www.google.com/maps/search/?api=1&query=${it.first},${it.second}"
                                                }

                                            val message =
                                                if (locationLink != null) {
                                                    """
                                                    SOS EMERGENCY ALERT - SAFEHER

                                                    I need immediate assistance. I may be in danger.
                                                    Please contact me immediately. If I cannot respond, please arrange help.

                                                    My current location:
                                                    $locationLink

                                                    Please use the location link above to find me.

                                                    Sent via SafeHer.
                                                    """.trimIndent()
                                                } else {
                                                    """
                                                    SOS EMERGENCY ALERT - SAFEHER

                                                    I need immediate assistance. I may be in danger.
                                                    Please contact me immediately. If I cannot respond, please arrange help.

                                                    My current location is unavailable.
                                                    Please try to contact me immediately.

                                                    Sent via SafeHer.
                                                    """.trimIndent()
                                                }

                                            onPrepareSms(
                                                contact.phoneNumber,
                                                message
                                            )
                                        },
                                        enabled = !isLocationLoading,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 48.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Pink,
                                            disabledContainerColor = TextSecondary
                                        ),
                                        shape = ButtonShape
                                    ) {
                                        if (isLocationLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(17.dp),
                                                color = White,
                                                strokeWidth = 2.dp
                                            )

                                            Spacer(Modifier.width(8.dp))

                                            Text("Getting location...")
                                        } else {
                                            Text(
                                                "Prepare SOS message",
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        locationStatus,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    if (isLocationLoading) {
                        Spacer(Modifier.height(8.dp))

                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = Pink
                        )
                    }

                    if (showLocationSettingsButton) {
                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Location permission is turned off. Enable it in Settings to share your current location.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = onOpenAppSettings,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Pink
                            ),
                            shape = ButtonShape
                        ) {
                            Text("Open App Settings")
                        }
                    }

                    locationCoordinates?.let { coordinates ->
                        Spacer(Modifier.height(8.dp))

                        Text(
                            "Latitude: ${coordinates.first}\nLongitude: ${coordinates.second}",
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Your messaging app will open with the message prepared. Review it and press Send yourself.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showContactsDialog = false
                        showSosConfirmation = true
                    }
                ) {
                    Text(
                        "Close",
                        color = Pink
                    )
                }
            },
            containerColor = White,
            shape = LargeShape
        )
    }

    // ------------------------------------------------
    // SOS COMPLETION DIALOG
    // ------------------------------------------------

    if (showSosConfirmation) {
        AlertDialog(
            onDismissRequest = {
                showSosConfirmation = false
            },
            title = {
                Text(
                    "SOS test complete",
                    fontWeight = FontWeight.Bold,
                    color = PinkDark
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 260.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(PinkLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "✓",
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }

                    Spacer(Modifier.height(13.dp))

                    Text(
                        "Your emergency contacts and location have been checked. You can prepare an SMS for a contact using the messaging app. Remember to review and send it yourself.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSosConfirmation = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Pink
                    ),
                    shape = ButtonShape
                ) {
                    Text("Done")
                }
            },
            containerColor = White,
            shape = LargeShape
        )
    }
}

// ----------------------------------------------------
// LOCATION VALUE COMPONENT
// ----------------------------------------------------

@Composable
private fun LocationValue(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                PinkPale,
                RoundedCornerShape(13.dp)
            )
            .padding(13.dp)
    ) {
        Eyebrow(label, Pink)

        Spacer(Modifier.height(5.dp))

        Text(
            value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

// ----------------------------------------------------
// DASHBOARD ACTION CARD
// ----------------------------------------------------

@Composable
fun DashboardActionCard(
    title: String,
    description: String,
    tag: String = "",
    symbol: String = "♡",
    iconBackground: Color = PinkLight,
    iconForeground: Color = Pink,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.heightIn(min = 148.dp),
        shape = CardShape,
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        border = BorderStroke(1.dp, Border),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp,
            pressedElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            FeatureIcon(
                symbol = symbol,
                background = iconBackground,
                foreground = iconForeground,
                size = 45.dp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                title,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                description,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
