# BookMemo — Kotlin Multiplatform (Android & iOS)

Une application mobile pour gérer sa collection de livres, mangas et animés — disponible sur Android et iOS à partir d'une base de code partagée.

---

## Pourquoi Kotlin Multiplatform ?

### Le problème à résoudre

Maintenir deux codebases séparées (Android + iOS) pour une application personnelle est coûteux : la logique métier (persistance locale, appels API, filtres, calculs de statistiques) doit être dupliquée, testée et corrigée deux fois. Le moindre bug de synchronisation entre les deux versions devient une source d'incohérence pour l'utilisateur.

### La solution : partager la logique, garder l'UI native

KMP permet de placer toute la couche **data** et **domain** dans un module `shared` compilé pour les deux plateformes. L'UI reste native sur chaque plateforme — ce qui garantit des performances et une expérience utilisateur conformes aux standards de chaque OS.

| Ce qui est partagé | Ce qui reste plateforme-spécifique |
|---|---|
| Modèles de données (`CollectionItem`, `SearchResult`) | `MainActivity.kt` (Android) |
| Repositories & Use Cases | `ContentView.swift` / `iOSApp.swift` (iOS) |
| ViewModels (`CollectionViewModel`) | Glance widget Android |
| Appels API Ktor (Google Books, MyAnimeList) | Driver SQLDelight (Android/iOS) |
| Base de données SQLDelight | Scanner code-barres GMS (Android) |
| Préférences utilisateur | Ktor engine (OkHttp / Darwin) |
| Navigation Compose Multiplatform | |
| Toute l'UI Compose (partagée Android + iOS) | |

### Problèmes concrets résolus

- **Zéro duplication de logique métier** : les règles de filtrage, la détection de doublons, le calcul des statistiques, la gestion des favoris — écrits une seule fois.
- **Cohérence garantie** : un bug corrigé dans `CollectionRepositoryImpl` est corrigé pour Android *et* iOS simultanément.
- **Onboarding simplifié** : un seul projet Kotlin à cloner, une seule suite de dépendances à comprendre.
- **Tests partagés** : les Use Cases et Repositories peuvent être testés dans `commonTest` indépendamment de toute plateforme.

---

## Fonctionnalités

- **Ajout manuel ou par scan ISBN** — scanner code-barres via GMS Code Scanner (Android)
- **Recherche en ligne** — Google Books API (livres), MyAnimeList API (mangas & animés)
- **Collection filtrée** — par type (livre / manga / animé), par statut (lu, en cours, souhaité), par favoris, par format
- **Suivi de progression** — tomes lus, chapitres lus, épisodes vus, cochage individuel des volumes
- **Vérification de nouveaux volumes** — comparaison avec les données MAL pour détecter de nouveaux tomes/épisodes
- **Sauvegarde cloud** — authentification Google Sign-In, backup/restore/suppression via Firebase Firestore
- **Statistiques** — vue agrégée de la collection
- **Export / Import CSV et base de données** — partage et migration de la collection depuis Android
- **Widget Android** — liste des favoris via Jetpack Glance
- **Thème pastel** — thème Material 3 personnalisé, commun Android et iOS, avec mode sombre/clair/système
- **Localisation** — français et anglais

---

## Stack technique

| Catégorie | Librairie | Version |
|---|---|---|
| Multiplatform | Kotlin Multiplatform | 2.4.10 |
| UI | Compose Multiplatform | 1.11.1 |
| UI Components | Material 3 | 1.10.0-alpha05 |
| DI | Koin | 4.2.2 |
| Base de données | SQLDelight | 2.3.2 |
| HTTP Client | Ktor | 3.5.1 |
| Sérialisation | kotlinx.serialization | 1.11.0 |
| Images | Coil 3 | 3.5.0 |
| Navigation | Navigation Compose (JetBrains) | 2.9.2 |
| Préférences | Multiplatform Settings | 1.3.0 |
| Date/Heure | kotlinx-datetime | 0.8.0 |
| Widget Android | Jetpack Glance | 1.1.1 |
| Scanner | GMS Code Scanner | 16.1.0 |
| Cloud Backup | Firebase BOM | 34.16.0 |
| Authentification | Firebase Auth + Google Sign-In (Credential Manager) | — |
| Analytics / Crashlytics | Firebase Crashlytics | 3.0.7 |

---

## Architecture

```
BookMemoKmp/
├── shared/                         # Module KMP partagé
│   └── src/
│       ├── commonMain/             # Code commun Android + iOS
│       │   ├── data/               # DTOs, mappers, repositories, SQLDelight
│       │   ├── domain/             # Modèles métier, use cases, interfaces
│       │   ├── presentation/       # ViewModels + UI Compose (écrans, composants)
│       │   ├── di/                 # Modules Koin
│       │   └── navigation/         # Écrans de navigation
│       ├── androidMain/            # Implémentations Android (driver DB, scanner...)
│       └── iosMain/                # Implémentations iOS (driver DB, Ktor Darwin...)
├── androidApp/                     # Point d'entrée Android (MainActivity, widget)
└── iosApp/                         # Point d'entrée iOS (SwiftUI wrapper)
```

Le projet suit une architecture **Clean Architecture** avec séparation stricte data / domain / presentation. Les ViewModels vivent dans `commonMain` et sont instanciés par Koin sur les deux plateformes.

---

## Lancer le projet

### Android

```shell
./gradlew :androidApp:assembleDebug
```

Ou directement depuis Android Studio avec la configuration de run `androidApp`.

### iOS

Ouvrir `iosApp/iosApp.xcodeproj` dans Xcode et lancer sur simulateur ou device.

> Pré-requis : Xcode 16+, Android Studio avec le plugin KMP, JDK 17+.

---

## Ce que j'ai appris en construisant ce projet

- Mettre en place une architecture KMP propre avec Koin pour l'injection de dépendances multi-plateforme
- Gérer les implémentations `expect/actual` pour les drivers SQLDelight, le scanner code-barres, et les moteurs Ktor
- Déboguer les contraintes des widgets Glance (incompatibilité Material3, pattern `flow.first()` vs `collect {}`)
- Faire cohabiter Compose Multiplatform pour l'UI partagée avec des points d'entrée natifs SwiftUI / Android
- Gérer les migrations de base de données SQLDelight (fichiers `.sqm`) sans casser les données existantes
- Intégrer Firebase Auth (Google Sign-In via Credential Manager) et Firestore pour la sauvegarde cloud
- Gérer les variantes de build Android (`debug`/`release` source sets) pour isoler les dépendances de développement (Firebase App Check debug provider)
- Consommer l'API MyAnimeList v2 (authentification par header, structure `data[].node`, endpoints de ranking et de détail)
