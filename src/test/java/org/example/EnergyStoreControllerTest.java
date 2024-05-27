package org.example;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = EnergyStoreController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityConfig.class})
@AutoConfigureMockMvc(addFilters = false)
// @Import(DisableSecurityConfiguration.class)
public class EnergyStoreControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnergyStoreService energyStoreService;

    @Captor
    private ArgumentCaptor<EnergyStore> energyStoreArgumentCaptor;

    @Test
    public void addEnergyStoreTest() throws Exception {
        mockMvc.perform(post("/api/energyStore").content("""
                { id: 1 }
                """)).andExpect(status().isOk());
        verify(energyStoreService, times(1)).addEnergyStore(energyStoreArgumentCaptor.capture());
        assertThat(energyStoreArgumentCaptor.getValue().getId()).isEqualTo(1L);
    }
}
