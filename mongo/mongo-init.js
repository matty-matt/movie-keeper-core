db.movies.createIndex({externalMovieId: 1}, {unique: true});
db.movies.createIndex({watched: 1}, {unique: false});
db.trailers.createIndex({externalMovieId: 1}, {unique: true});
db.cast.createIndex({externalMovieId: 1}, {unique: true});