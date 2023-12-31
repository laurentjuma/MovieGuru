/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.frogtest.movieguru.data.repository

import com.frogtest.movieguru.domain.model.UserSettings
import com.frogtest.movieguru.domain.repository.UserSettingsRepository
import com.frogtest.movieguru.preferences.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserSettingsRepositoryImpl @Inject constructor(
    private val preferencesDataSource: SettingsDataStore,
) : UserSettingsRepository {

    override val userSettings: Flow<UserSettings> = preferencesDataSource.userSettings

    override suspend fun setDarkThemeConfig(darkThemeConfig: String) {
        preferencesDataSource.setDataThemeConfig(darkThemeConfig)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        preferencesDataSource.toggleUseDynamicColor(useDynamicColor)
    }

    override suspend fun useGrid(useGrid: Boolean) {
        preferencesDataSource.toggleUseGrid(useGrid)
    }

    override suspend fun useFingerPrint(useFingerPrint: Boolean) {
        preferencesDataSource.toggleUseFingerPrint(useFingerPrint)
    }

    override suspend fun toggleSort(sort: Boolean) {
        preferencesDataSource.toggleSort(sort)
    }


}
