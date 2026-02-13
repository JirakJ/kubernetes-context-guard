package com.jakubjirak.k8sguard.git

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BranchClusterMapperTest {

    private val mapper = BranchClusterMapper()

    @Test
    fun `feature branch maps to dev cluster`() {
        val mapping = mapper.findMapping("feature/login")
        assertNotNull(mapping)
        assertEquals("dev-cluster", mapping!!.targetContext)
        assertFalse(mapping.autoSwitch)
    }

    @Test
    fun `develop branch maps to staging cluster`() {
        val mapping = mapper.findMapping("develop")
        assertNotNull(mapping)
        assertEquals("staging-cluster", mapping!!.targetContext)
    }

    @Test
    fun `main branch maps to staging cluster`() {
        val mapping = mapper.findMapping("main")
        assertNotNull(mapping)
        assertEquals("staging-cluster", mapping!!.targetContext)
        assertTrue(mapping.requireConfirmation)
    }

    @Test
    fun `master branch maps to staging cluster`() {
        val mapping = mapper.findMapping("master")
        assertNotNull(mapping)
        assertEquals("staging-cluster", mapping!!.targetContext)
        assertTrue(mapping.requireConfirmation)
    }

    @Test
    fun `release branch maps to prod cluster`() {
        val mapping = mapper.findMapping("release/2.0.0")
        assertNotNull(mapping)
        assertEquals("prod-cluster", mapping!!.targetContext)
        assertTrue(mapping.requireConfirmation)
    }

    @Test
    fun `hotfix branch maps to prod cluster`() {
        val mapping = mapper.findMapping("hotfix/urgent-fix")
        assertNotNull(mapping)
        assertEquals("prod-cluster", mapping!!.targetContext)
    }

    @Test
    fun `unknown branch returns null mapping`() {
        val mapping = mapper.findMapping("random-branch")
        assertNull(mapping)
    }

    @Test
    fun `status is MATCHED when branch maps to current context`() {
        val status = mapper.getStatus("feature/login", "dev-cluster")
        assertEquals(MatchStatus.MATCHED, status.matchStatus)
        assertEquals("dev-cluster", status.expectedContext)
    }

    @Test
    fun `status is MISMATCHED when branch maps to different context`() {
        val status = mapper.getStatus("feature/login", "prod-cluster")
        assertEquals(MatchStatus.MISMATCHED, status.matchStatus)
        assertEquals("dev-cluster", status.expectedContext)
    }

    @Test
    fun `status is UNMAPPED for unknown branch`() {
        val status = mapper.getStatus("random-branch", "dev-cluster")
        assertEquals(MatchStatus.UNMAPPED, status.matchStatus)
        assertNull(status.expectedContext)
    }

    @Test
    fun `pattern matching is exact for non-glob patterns`() {
        assertTrue(BranchClusterMapper.matchesBranchPattern("main", "main"))
        assertFalse(BranchClusterMapper.matchesBranchPattern("main-backup", "main"))
    }

    @Test
    fun `pattern matching supports wildcard`() {
        assertTrue(BranchClusterMapper.matchesBranchPattern("feature/login", "feature/*"))
        assertTrue(BranchClusterMapper.matchesBranchPattern("feature/user-signup", "feature/*"))
        assertFalse(BranchClusterMapper.matchesBranchPattern("bugfix/login", "feature/*"))
    }

    @Test
    fun `custom mappings override defaults`() {
        val customMappings = listOf(
            BranchClusterMapping("feature/*", "custom-dev", autoSwitch = true),
        )
        val customMapper = BranchClusterMapper(customMappings)
        val mapping = customMapper.findMapping("feature/test")
        assertNotNull(mapping)
        assertEquals("custom-dev", mapping!!.targetContext)
        assertTrue(mapping.autoSwitch)
    }

    @Test
    fun `first matching mapping wins`() {
        val mappings = listOf(
            BranchClusterMapping("feature/*", "first-cluster"),
            BranchClusterMapping("feature/*", "second-cluster"),
        )
        val customMapper = BranchClusterMapper(mappings)
        val mapping = customMapper.findMapping("feature/test")
        assertEquals("first-cluster", mapping!!.targetContext)
    }

    @Test
    fun `status preserves branch and context names`() {
        val status = mapper.getStatus("develop", "staging-cluster")
        assertEquals("develop", status.currentBranch)
        assertEquals("staging-cluster", status.currentContext)
    }
}
