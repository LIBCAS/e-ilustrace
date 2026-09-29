package cz.inqool.eas.common.domain.index;

import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Utility methods for working with index query.
 */
public class QueryUtils {

    /**
     * Builds AND query over provided child queries.
     *
     * @param queries Child queries
     */
    public static BoolQueryBuilder andQuery(List<QueryBuilder> queries) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        queries.
                stream().
                filter(Objects::nonNull).
                forEach(query::must);


        return query;
    }

    /**
     * Builds OR query over provided child queries.
     *
     * @param queries Child queries
     */
    public static BoolQueryBuilder orQuery(List<QueryBuilder> queries) {
        if (queries.size() == 0) {
            return QueryBuilders.boolQuery().mustNot(QueryBuilders.matchAllQuery());
        }

        BoolQueryBuilder query = QueryBuilders.boolQuery();

        queries.
                stream().
                filter(Objects::nonNull).
                forEach(query::should);

        return query;
    }

    /**
     * Returns a String where special ElasticSearch characters are escaped.
     */
    public static String escapeAll(String s) {
        return QueryParser.escape(s);
    }

    /**
     * Returns a String where special ElasticSearch characters are escaped (allowed are only '*' and '?').
     */
    public static String escapeAllButWildcard(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // These characters are part of the query syntax and must be escaped
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
                    || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
                    || c == '|' || c == '&' || c == '/') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Lower-cases provided string.
     *
     * @param s Provided string
     * @return Lower-cased string
     */
    public static String lowercase(@Nullable String s) {
        return s != null ? s.toLowerCase() : null;
    }

    /**
     * Applies ASCII folding to a string.
     *
     * @param s Provided string
     * @return Folded string
     */
    public static String asciiFolding(String s) {
        if (s != null) {
            int length = s.length();
            char[] unfoldedChars = s.toCharArray();
            char[] foldedChars = new char[length * 4]; // according to "foldToASCII" method should be of size >= length * 4
            ASCIIFoldingFilter.foldToASCII(unfoldedChars, 0, foldedChars, 0, length);
            return new String(foldedChars, 0, length);
        } else {
            return null;
        }
    }
}
