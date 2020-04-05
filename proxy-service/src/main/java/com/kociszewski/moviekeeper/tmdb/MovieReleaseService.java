package com.kociszewski.moviekeeper.tmdb;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MovieReleaseService {

    private static final String US = "US";
    private static final String GB = "GB";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public String digitalRelease(ReleasesResult releasesResult) {
        return releasesResult.getResults()
                .stream()
                .filter(release -> isEnglishRelease(release) && hasDigitalRelease(release))
                .map(CountryRelease::getReleaseDates)
                .map(this::onlyDigitalReleases)
                .map(date -> LocalDateTime.parse(date, FORMATTER))
                .min(LocalDateTime::compareTo)
                .map(LocalDateTime::toString).orElse(null);
    }

    private boolean isEnglishRelease(CountryRelease release) {
        return US.equals(release.getCountry()) || GB.equals(release.getCountry());
    }

    private boolean hasDigitalRelease(CountryRelease release) {
        return release.getReleaseDates().stream().anyMatch(this::isReleaseDigital);
    }

    private boolean isReleaseDigital(TmdbRelease tmdbRelease) {
        return ReleaseType.DIGITAL.getTypeId() == tmdbRelease.getType();
    }

    private String onlyDigitalReleases(List<TmdbRelease> tmdbReleases) {
        return tmdbReleases
                .stream()
                .filter(this::isReleaseDigital)
                .map(TmdbRelease::getReleaseDate)
                .findAny()
                .orElse(null);
    }
}
