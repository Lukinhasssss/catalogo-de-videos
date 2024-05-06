package com.lukinhasssss.catalogo.infrastructure.castmember.persistence

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface CastMemberRepository : ElasticsearchRepository<CastMemberDocument, String>
