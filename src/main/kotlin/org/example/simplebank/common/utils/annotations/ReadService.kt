package org.example.simplebank.common.utils.annotations

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
annotation class ReadService
