db.movies.createIndex({externalMovieId: 1}, {unique: true});
db.trailers.createIndex({externalMovieId: 1}, {unique: true});
db.cast.createIndex({externalMovieId: 1}, {unique: true});