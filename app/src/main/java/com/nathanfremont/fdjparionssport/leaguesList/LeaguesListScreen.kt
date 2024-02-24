package com.nathanfremont.fdjparionssport.leaguesList

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import com.nathanfremont.domain.repository.Team
import com.nathanfremont.fdjparionssport.R
import com.nathanfremont.fdjparionssport.common.ComposeOverlay
import com.nathanfremont.fdjparionssport.common.toReadableString
import com.nathanfremont.fdjparionssport.ui.theme.SportColor

interface LeaguesListActions {
    fun onSearchValueChanged(newTextFieldValue: TextFieldValue)
    fun onAutoCompleteOptionClicked(autoCompleteOption: String)
}

@Composable
fun LeaguesListScreen(
    leaguesListViewModel: LeaguesListViewModel = hiltViewModel(),
) {
    val leaguesListState: LeaguesListState by leaguesListViewModel
        .state
        .collectAsState()

    var mutableSearchValue: String by remember {
        mutableStateOf("")
    }

    var textFieldSelection by remember {
        mutableStateOf(TextRange.Zero)
    }

    val onSearchValueChanged: (TextFieldValue) -> Unit = { newTextFieldValue: TextFieldValue ->
        mutableSearchValue = newTextFieldValue.text
        textFieldSelection = newTextFieldValue.selection
        leaguesListViewModel.searchLeague(
            searchValue = mutableSearchValue,
        )
    }
    LeaguesListContent(
        leaguesListState = leaguesListState,
        searchTextValue = TextFieldValue(
            text = mutableSearchValue,
            selection = textFieldSelection,
        ),
        leaguesListActions = object : LeaguesListActions {
            override fun onSearchValueChanged(newTextFieldValue: TextFieldValue) {
                onSearchValueChanged(newTextFieldValue)
            }

            override fun onAutoCompleteOptionClicked(autoCompleteOption: String) {
                onSearchValueChanged(
                    TextFieldValue(
                        text = autoCompleteOption,
                        selection = TextRange(
                            autoCompleteOption.length,
                        ),
                    )
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaguesListContent(
    leaguesListState: LeaguesListState,
    searchTextValue: TextFieldValue,
    leaguesListActions: LeaguesListActions,
) {
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
            ) {
                TextField(
                    value = searchTextValue,
                    onValueChange = leaguesListActions::onSearchValueChanged,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.leagues_list_search_hint),
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    }
                ) {
                    leaguesListState
                        .autoCompleteOptions
                        .forEach { selectionOption ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = selectionOption)
                                },
                                onClick = {
                                    leaguesListActions.onAutoCompleteOptionClicked(
                                        autoCompleteOption = selectionOption,
                                    )
                                    expanded = false
                                },
                            )
                        }
                }
            }
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(count = 2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
            ) {
                items(leaguesListState.teams) { team: Team ->
                    TeamItem(
                        team = team,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                    )
                }
            }

        }

        if (leaguesListState.isLoading) {
            ComposeOverlay {
                CircularProgressIndicator()
            }
        }

        LaunchedEffect(key1 = leaguesListState.error) {
            if (leaguesListState.error != null) {
                Toast.makeText(
                    context,
                    leaguesListState.error.errorTitle.toReadableString(context),
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }
}

@Composable
private fun TeamItem(
    team: Team,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White,
        ),
        elevation = CardDefaults.elevatedCardElevation(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = SportColor.GreyCCC,
        ),
        modifier = modifier,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize(),
        ) {
            SubcomposeAsyncImage(
                model = team.teamBadge,
                contentDescription = null,
                loading = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = team.teamName,
                            style = TextStyle.Default,
                            fontSize = 18.sp,
                        )
                    }
                },
                error = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        Text(
                            text = team.teamName,
                            style = TextStyle.Default,
                            fontSize = 18.sp,
                        )
                    }
                },
                modifier = Modifier
                    .matchParentSize()
                    .padding(16.dp),
            )
        }
    }
}

@Composable
@Preview
private fun PreviewList() {
    LeaguesListContent(
        leaguesListState = LeaguesListState(
            isLoading = false,
            error = null,
            teams = listOf(
                Team(
                    idTeam = "idTeam",
                    teamName = "Team name",
                    teamBadge = "someUrl"
                )
            ),
            autoCompleteOptions = emptyList(),
        ),
        searchTextValue = TextFieldValue(
            text = "",
        ),
        leaguesListActions = object : LeaguesListActions {
            override fun onSearchValueChanged(newTextFieldValue: TextFieldValue) {}

            override fun onAutoCompleteOptionClicked(autoCompleteOption: String) {}
        },
    )
}