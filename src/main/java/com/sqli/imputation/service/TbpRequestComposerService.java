package com.sqli.imputation.service;

import com.sqli.imputation.service.dto.TbpRequestBodyDTO;
import java.util.List;

public interface TbpRequestComposerService {

    List<TbpRequestBodyDTO> divideTbpPeriod(TbpRequestBodyDTO tbpRequestBodyDTO);
}
