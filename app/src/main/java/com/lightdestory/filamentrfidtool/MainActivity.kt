package com.lightdestory.filamentrfidtool

import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lightdestory.filamentrfidtool.ui.components.NfcStatusDialog
import com.lightdestory.filamentrfidtool.ui.theme.FilamentRFIDToolTheme
import com.lightdestory.filamentrfidtool.ui.views.about.AboutScreen
import com.lightdestory.filamentrfidtool.ui.views.scanner.ScannerScreen
import com.lightdestory.filamentrfidtool.ui.views.vault.VaultScreen
import com.lightdestory.filamentrfidtool.utils.openNfcSettings
import com.lightdestory.filamentrfidtool.utils.registerNfcStateReceiver
import com.lightdestory.filamentrfidtool.utils.resolveNfcStatus

class MainActivity : ComponentActivity() {
    private val latestNfcIntent = mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        latestNfcIntent.value = intent
        enableEdgeToEdge()
        setContent {
            val rememberedIntent = rememberUpdatedState(latestNfcIntent.value)
            FilamentRFIDToolTheme { MainScaffold(rememberedIntent) }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        latestNfcIntent.value = intent
    }
}

private enum class BottomDestination(val route: String, val labelRes: Int) {
    Scanner("scanner", R.string.navigation_scanner),
    Vault("vault", R.string.navigation_vault),
    About("about", R.string.navigation_about)
}

@Composable
private fun MainScaffold(
    latestNfcIntent: androidx.compose.runtime.State<Intent?> = remember {
        mutableStateOf(null)
    },
    showNfcPrompt: Boolean = true
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val items = listOf(BottomDestination.Scanner, BottomDestination.Vault, BottomDestination.About)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { destination ->
                    NavigationBarItem(
                        selected =
                            currentDestination?.hierarchy?.any {
                                it.route == destination.route
                            } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(stringResource(destination.labelRes)) },
                        icon = {
                            when (destination) {
                                BottomDestination.Scanner ->
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Default.Nfc,
                                        contentDescription =
                                            stringResource(destination.labelRes)
                                    )

                                BottomDestination.Vault ->
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Default.Storage,
                                        contentDescription =
                                            stringResource(destination.labelRes)
                                    )

                                BottomDestination.About ->
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription =
                                            stringResource(destination.labelRes)
                                    )
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        if (showNfcPrompt) {
            NfcPrompt(onOpenSettings = { openNfcSettings(context) })
        }
        NavHost(
            navController = navController,
            startDestination = BottomDestination.Scanner.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomDestination.Scanner.route) { ScannerScreen(latestNfcIntent) }
            composable(BottomDestination.Vault.route) { VaultScreen() }
            composable(BottomDestination.About.route) { AboutScreen() }
        }
    }
}

@Composable
fun NfcPrompt(onOpenSettings: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var nfcStatus by remember { mutableStateOf(resolveNfcStatus(context)) }
    var stateReceiver by remember { mutableStateOf<BroadcastReceiver?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                nfcStatus = resolveNfcStatus(context)
            }
            if (event == Lifecycle.Event.ON_RESUME && stateReceiver == null) {
                stateReceiver = registerNfcStateReceiver(context) { status -> nfcStatus = status }
            }
            if (event == Lifecycle.Event.ON_PAUSE) {
                stateReceiver?.let { context.unregisterReceiver(it) }
                stateReceiver = null
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            stateReceiver?.let { context.unregisterReceiver(it) }
            stateReceiver = null
        }
    }

    NfcStatusDialog(
        status = nfcStatus,
        onOpenSettings = onOpenSettings
    )
}

@Preview(showBackground = true)
@Composable
private fun MainScaffoldPreview() {
    FilamentRFIDToolTheme { MainScaffold(showNfcPrompt = false) }
}
