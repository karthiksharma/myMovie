Add the below lines of code to build.gradle file in your app folder, also replace [YOUR_MOVIE_DB_API_KEY] with your API key


buildTypes.each {
        it.buildConfigField 'String', 'MOVIE_API_KEY', '"[YOUR_MOVIE_DB_API_KEY]"'
    }


P.S. the MOVIE_API_KEY is the key it should not be changed only replace with brackets.

Alternatively API key can be placed in following files:
1. DetailActivityFragment.java, line no 72 replace BuildConfig.MOVIE_API_KEY with your api key.
2. MainActivityFragment.java, line no 87 replace BuildConfig.MOVIE_API_KEY with your api key.