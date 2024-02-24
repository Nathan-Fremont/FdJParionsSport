package com.nathanfremont.fdjparionssport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nathanfremont.fdjparionssport.leaguesList.LeaguesListScreen
import com.nathanfremont.fdjparionssport.ui.theme.FdjParionsSportTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FdjParionsSportTheme {
                LeaguesListScreen()
            }
        }
    }
}