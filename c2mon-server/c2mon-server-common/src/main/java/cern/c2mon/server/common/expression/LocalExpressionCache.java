package cern.c2mon.server.common.expression;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import cern.c2mon.server.common.tag.Tag;
import cern.c2mon.shared.client.expression.Expression;
import groovy.lang.GroovyObject;
import lombok.extern.slf4j.Slf4j;

/**
 * Stores all relevant information of a compiled expression.
 * Because the time to compile an expression is to high we need to
 * store a compiled expression on a local node.
 *
 * @author Franz Ritter
 */
@Slf4j
public class LocalExpressionCache {

  /**
   * Holds the information to which time the expression got compiled the last time.
   */
  private static Map<Long, Map<String, Long>> tagIdToVersion = new ConcurrentHashMap<>();

  /**
   * Holds the compiled expressions.
   */
  private static Map<Long, Map<String, GroovyObject>> tagIdToLocalScripts = new ConcurrentHashMap<>();


  private LocalExpressionCache() {
  }

  /**
   * Get all tag ids with locally stored expressions.
   *
   * @return the tag ids of all locally stored expressions
   */
  public static Set<Long> getAllIds() {
    return tagIdToLocalScripts.keySet();
  }

  /**
   * Removes all local expression information of the tag to the given id.
   *
   * @param id The id of the tag which expression information needs to be deleted
   */
  public static void removeTag(Long id) {
    tagIdToLocalScripts.remove(id);
    tagIdToVersion.remove(id);
  }

  /**
   * Removes all local expression information of the tags to the given ids.
   *
   * @param tagIds The ids of the tags which expression information needs to be deleted
   */
  public static void removeTags(List<Long> tagIds) {
    tagIds.stream().forEach(LocalExpressionCache::removeTag);
  }

  protected static GroovyObject getScript(Long tagId, String expressionName) {
    return tagIdToLocalScripts.get(tagId).get(expressionName);
  }

  protected static <T extends Tag> void initializeTag(T tag) {
    Long tagId = tag.getId();

    if (tagIdToVersion.get(tagId) == null) {
      tagIdToVersion.put(tagId, new ConcurrentHashMap<>());
    }
    if (tagIdToLocalScripts.get(tagId) == null) {
      tagIdToLocalScripts.put(tagId, new ConcurrentHashMap<>());
    }
    purgeRemovedExpressions(tag);
  }

  protected static <T extends Tag> void addExpressionToTag(T tag, Expression expression) {
    Long version = tagIdToVersion.get(tag.getId()).get(expression.getName());
    version = version == null ? -1L : version;

    if (version < expression.getVersion()) {
      GroovyObject script = ExpressionFactory.createScript(expression.getExpression());

      tagIdToLocalScripts.get(tag.getId()).put(expression.getName(), script);
      tagIdToVersion.get(tag.getId()).put(expression.getName(), expression.getVersion());
    }
  }

  private static <T extends Tag> void purgeRemovedExpressions(T tag) {
    Long tagId = tag.getId();
    Map<String, GroovyObject> scripts = tagIdToLocalScripts.get(tagId);

    if (tag.getExpressions().size() < scripts.size()) {
      List<String> expressionsToPurge = getExpressionsToPurge(tagId, tag.getExpressions());

      for (String expression : expressionsToPurge) {
        log.debug("Remove the script {} of the tag {} from the local cache", expression, tagId);
        tagIdToLocalScripts.get(tagId).remove(expression);
        tagIdToVersion.get(tagId).remove(expression);
      }
    }
  }

  private static List<String> getExpressionsToPurge(Long tagId, Collection<Expression> expressions) {
    List<String> expressionsToPurge = new ArrayList<>();
    Map<String, GroovyObject> scripts = tagIdToLocalScripts.get(tagId);

    for (String localExpressionName : scripts.keySet()) {
      boolean isInExpression = false;
      for (Expression cacheExpression : expressions) {
        isInExpression |= cacheExpression.getName().equals(localExpressionName);
      }

      if (!isInExpression) {
        expressionsToPurge.add(localExpressionName);
      }
    }
    return expressionsToPurge;
  }
}
